package com.drimase.datacollector.ui.main

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import android.location.LocationListener
import android.util.Log
import androidx.camera.core.*
import androidx.lifecycle.*
import com.drimase.datacollector.*
import com.drimase.datacollector.dto.AccidentProneArea
import com.drimase.datacollector.service.GpsService
import com.drimase.datacollector.service.UserManager
import com.drimase.datacollector.util.ProgressRequestBody
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File
import javax.inject.Inject

private const val TAG = "MainViewModel"

@SuppressLint("RestrictedApi")
class MainViewModel @Inject constructor(
        application: Application,
        private val repository: Repository,
) : AndroidViewModel(application) {
    lateinit var imageCapture: ImageCapture
    lateinit var videoCapture: VideoCapture

    @Inject
    lateinit var userManager: UserManager

    @Inject
    lateinit var gpsService: GpsService

    @Inject
    lateinit var dirs : File

    var recording = false
    val recordText = MutableLiveData<String>("녹화")

    val logAlert = MutableLiveData<LogAlert>()

    @Inject
    lateinit var location: MutableLiveData<Location>


    val alert = MutableLiveData<Unit>()
    private val accidentProneArea  = MutableLiveData<List<AccidentProneArea>>()
    private val detectedAPA = ArrayList<AccidentProneArea>()
    private val mDisposable = CompositeDisposable()

    val onProgress = MutableLiveData<Boolean>(false)
    val progressValue = MutableLiveData<Float>(0.0f)


    private val locationObserver = Observer<Location> {
        if (recording && userManager.getRecordingVideoID() > 0) {
            captureVideoFrame()
        }
    }

    //단일 사진 촬영
    fun takePhoto(){
        if(locationServiceUnavailable() || onUpload())
            return

        val file = File(dirs,"${System.currentTimeMillis()}.jpeg")
        imageCapture.takePicture(file, object : ImageCapture.OnImageSavedListener {
            override fun onImageSaved(file: File) {
                Log.i(TAG, "Image File : $file")
                logFrameLocation(file,LogType.SINGLE_FRAME)
            }

            override fun onError(
                useCaseError: ImageCapture.UseCaseError,
                message: String,
                cause: Throwable?
            ) {
                Log.i(TAG, "Video Error: $message")
            }
        })
    }

    @SuppressLint("RestrictedApi")
    fun onRecordClick(){
        if(recording){
            stopVideoLog()
        }else{
            if(locationServiceUnavailable() || onUpload())
                return
            startVideoLog()
        }
    }

    fun detect(){
        location.value?.let {location ->
            repository.detect(location)
                    .subscribe({
                        accidentProneArea.value = it
                        if(checkAlerted(it)){
                            alert.value = Unit
                        }
                    }, {
                        Log.d(TAG, "detect: ${it.message}")
                    })
        }
    }

    fun stopVideoLog(){
        location.removeObserver(locationObserver)
        recordText.value = "녹화"
        videoCapture.stopRecording()
        recording=!recording
    }

    private fun startVideoLog(){
        recording=!recording
        recordText.value = "중지"
        setRecordingVideoIdToUserManager()
        location.observeForever(locationObserver)
        val file = File(dirs,"${System.currentTimeMillis()}.mp4")
        videoCapture.startRecording(file, object : VideoCapture.OnVideoSavedListener {
            override fun onVideoSaved(file: File) {
                Log.i(TAG, "Video File : $file")
                onVideoStopped(file)
            }

            override fun onError(
                    useCaseError: VideoCapture.UseCaseError?,
                    message: String?,
                    cause: Throwable?
            ) {
                Log.i(TAG, "Video Error: $message")
            }
        })
    }


    private fun setRecordingVideoIdToUserManager(){
        val disposable = repository.startVideoLog(userManager.getUserId()).subscribe({
            userManager.setRecordingVideoID(it.id)
        },{
            it.stackTrace
        })
        mDisposable.add(disposable)
    }

    //영상 프레임 캡쳐
    private fun captureVideoFrame(){
        val file = File(dirs,"${System.currentTimeMillis()}.jpeg")

        imageCapture.takePicture(file, object : ImageCapture.OnImageSavedListener {
            override fun onImageSaved(file: File) {
                Log.i(TAG, "Image File : $file")
                logFrameLocation(file,LogType.SINGLE_FRAME)
            }

            override fun onError(
                    useCaseError: ImageCapture.UseCaseError,
                    message: String,
                    cause: Throwable?
            ) {
                Log.i(TAG, "Video Error: $message")
            }
        })
    }

    //위치 및 사진 정보 서버로 전송
    private fun logFrameLocation(file: File, logType: LogType){
        val location = location.value!!
        val videoId = if(logType == LogType.SINGLE_FRAME) SINGLE_IMAGE else userManager.getRecordingVideoID()
        val disposable = repository.logFrameLocation(
                userManager.getUserId(),
                videoId,
                location.longitude,
                location.latitude,
                file
        ).subscribe({
            logAlert.value = LogAlert.SUCCESS
            Log.d(TAG,it.toString())
        }, {
            logAlert.value = LogAlert.FAIL
            it.stackTrace
            Log.d(TAG,it.message.toString())
        })
        mDisposable.add(disposable)
    }

    //비디오 종료 시 API 호출하여 서버로 전송
    private fun onVideoStopped(file:File){
        onProgress.postValue(true)
        val location = location.value!!
        val videoPartFile = ProgressRequestBody(file)
        videoPartFile.getProgressSubject()
                .subscribeOn(Schedulers.io())
                .subscribe {
                    Log.i("PROGRESS", "stopVideoLog: $it")
                    progressValue.postValue(it)
                }

        val disposable = repository.stopVideoLog(
                userManager.getUserId(),
                userManager.getRecordingVideoID(),
                location.longitude,
                location.latitude,
                videoPartFile
        ).subscribe({
            logAlert.value = LogAlert.SUCCESS
            onProgress.value = false
        }, {
            logAlert.value = LogAlert.VIDEO_LOG_FAIL
            onProgress.value = false
            Log.i(TAG, "stopVideoLog: ${it.message}")
        })
        mDisposable.add(disposable)
    }

    private fun checkAlerted(list : List<AccidentProneArea>) : Boolean{
        for(area in list){
            if(!detectedAPA.contains(area)){
                detectedAPA.add(area)
                return true
            }
        }
        return false
    }

    fun setImageCapture(){
        val imageCaptureConfig= ImageCaptureConfig.Builder()
                .setLensFacing(CameraX.LensFacing.BACK)
                .build()

        imageCapture = ImageCapture(imageCaptureConfig)
    }

    @SuppressLint("RestrictedApi")
    fun setVideoCapture(){
        val videoCaptureConfig= VideoCaptureConfig.Builder()
                .setLensFacing(CameraX.LensFacing.BACK)
                .build()

        videoCapture = VideoCapture(videoCaptureConfig)
    }

    private fun locationServiceUnavailable() : Boolean{
        if(location.value == null){
            logAlert.value = LogAlert.LOCATION_UNAVAILABLE
            return true
        }
        return false
    }

    private fun onUpload() : Boolean{
        if(onProgress.value == true){
            logAlert.value = LogAlert.ON_PROGRESS
            return true
        }
        return false
    }

    override fun onCleared() {
        mDisposable.clear()
        super.onCleared()
    }

    companion object{
        const val SINGLE_IMAGE = 0
    }

}

enum class LogAlert {
    SUCCESS,FAIL,FRAME_LOG_FAIL,VIDEO_LOG_FAIL,LOCATION_UNAVAILABLE,ON_PROGRESS
}

enum class LogType {
    SINGLE_FRAME, VIDEO_FRAME
}
