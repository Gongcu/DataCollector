package com.drimase.datacollector.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
import androidx.camera.core.*
import androidx.lifecycle.*
import com.drimase.datacollector.*
import com.drimase.datacollector.dto.AccidentProneArea
import com.drimase.datacollector.dto.User
import com.drimase.datacollector.ui.login.LoginViewModel
import com.drimase.datacollector.util.ProgressRequestBody
import com.tbruyelle.rxpermissions3.RxPermissions
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

    private val location : MutableLiveData<Location> by lazy{
        gpsService.locationLiveData
    }

    val accidentProneArea  = MutableLiveData<List<AccidentProneArea>>()
    val alert = MutableLiveData<Unit>()
    private val detectedAPA = ArrayList<AccidentProneArea>()
    private val mDisposable = CompositeDisposable()

    val networking = MutableLiveData<Boolean>(false)
    val progress = MutableLiveData<Float>(0.0f)

    private val locationObserver = Observer<Location> {
        Log.i(TAG,"LOCATION OBSERVER : ${recording} ${userManager.getRecordingVideoID()}")
        if (recording && userManager.getRecordingVideoID() > 0) {
            captureFrame()
        }
    }


    fun takePhoto(){
        val file = File(dirs,"${System.currentTimeMillis()}.jpeg")
        imageCapture.takePicture(file, object : ImageCapture.OnImageSavedListener {
            override fun onImageSaved(file: File) {
                Log.i(TAG, "Image File : $file")
                logImageLocation(file)
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
            recordText.value = "녹화"
            videoCapture.stopRecording()
            location.removeObserver(locationObserver)
        }else{
            recordText.value = "중지"
            defineRecordingVideoId()
            location.observeForever(locationObserver)
            val file = File(dirs,"${System.currentTimeMillis()}.mp4")
            videoCapture.startRecording(file, object : VideoCapture.OnVideoSavedListener {
                override fun onVideoSaved(file: File) {
                    Log.i(TAG, "Video File : $file")
                    stopVideoLog(file)
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
        recording=!recording
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




    private fun logImageLocation(file: File){
        val location = gpsService.locationLiveData.value!!
        val disposable = repository.logImageLocation(
                userManager.getUserId(),
                location.longitude,
                location.latitude,
                file
        ).subscribe(
                {
                    logAlert.value = LogAlert.SUCCESS
                },
                {
                    logAlert.value = LogAlert.FAIL
                    it.stackTrace
                })
        mDisposable.add(disposable)
    }

    private fun defineRecordingVideoId(){
        val disposable = repository.startVideoLog(userManager.getUserId()).subscribe({
            userManager.setRecordingVideoID(it.id)
        },{
            it.stackTrace
        })
        mDisposable.add(disposable)
    }

    private fun captureFrame(){
        val file = File(dirs,"${System.currentTimeMillis()}.jpeg")

        imageCapture.takePicture(file, object : ImageCapture.OnImageSavedListener {
            override fun onImageSaved(file: File) {
                Log.i(TAG, "Image File : $file")
                logVideoFrameLocation(file)
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

    private fun logVideoFrameLocation(file: File){
        val location = gpsService.locationLiveData.value!!
        val disposable = repository.logVideoFrameLocation(
                userManager.getUserId(),
                userManager.getRecordingVideoID(),
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

    private fun stopVideoLog(file:File){
        networking.postValue(true)
        val location = gpsService.locationLiveData.value!!
        val videoPartFile = ProgressRequestBody(file)
        videoPartFile.getProgressSubject()
                .subscribeOn(Schedulers.io())
                .subscribe {
                    Log.i("PROGRESS", "stopVideoLog: $it")
                    progress.postValue(it)
                }

        val disposable = repository.stopVideoLog(
                userManager.getUserId(),
                userManager.getRecordingVideoID(),
                location.longitude,
                location.latitude,
                videoPartFile
        ).subscribe({
            logAlert.value = LogAlert.SUCCESS
            networking.value = false
        }, {
            logAlert.value = LogAlert.FAIL
            networking.value = false
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

    override fun onCleared() {
        mDisposable.clear()
        super.onCleared()
    }

}

enum class LogAlert {
    SUCCESS,FAIL
}
