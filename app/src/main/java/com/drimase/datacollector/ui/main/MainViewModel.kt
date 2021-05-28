package com.drimase.datacollector.ui.main

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import android.util.Log
import androidx.camera.core.*
import androidx.lifecycle.*
import com.drimase.datacollector.dto.AccidentProneArea
import com.drimase.datacollector.service.*
import com.drimase.datacollector.dto.ProgressRequestBody
import com.drimase.datacollector.dto.VideoRecord
import com.drimase.datacollector.repository.Repository
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File
import javax.inject.Inject


@SuppressLint("RestrictedApi")
class MainViewModel @Inject constructor(
    application: Application,
    private val repository: Repository,
) : AndroidViewModel(application) {

    @Inject
    lateinit var userManager: UserManager

    @Inject
    lateinit var gpsService: LocationService

    @Inject
    lateinit var sensorManager: SensorManager

    @Inject
    lateinit var dirs : File

    @Inject
    lateinit var sharedPreferencesManager: SharedPreferencesManager

    var recording = false

    val logAlert = MutableLiveData<LogAlert>()

    @Inject
    lateinit var location: MutableLiveData<Location>
    @Inject
    lateinit var altitude: MutableLiveData<Float>

    val accidentProneAreaAlert = MutableLiveData<Unit>()
    private val accidentProneArea  = MutableLiveData<List<AccidentProneArea>>()
    private val detectedAPA = ArrayList<AccidentProneArea>()
    private val mDisposable = CompositeDisposable()

    val onProgress = MutableLiveData<Boolean>(false)
    val progressValue = MutableLiveData<Float>(0.0f)


    val logout = MutableLiveData<Unit>()


    //주변 사고 다발 지역 확인
    fun detectAccidentProneArea(){
        location.value?.let {location ->
            repository.detect(location)
                    .subscribe({
                        accidentProneArea.value = it
                        if(checkAlerted(it)){
                            accidentProneAreaAlert.value = Unit
                        }
                    }, {
                        Log.d(TAG, "detect: ${it.message}")
                    })
        }
    }

    //현재 촬영중인 영상 ID 설정
    fun setRecordingVideoIdToUserManager(){
        val disposable = repository.startVideoLog(userManager.getUserId()).subscribe({
            userManager.setRecordingVideoID(it.id)
        },{
            it.stackTrace
        })
        mDisposable.add(disposable)
    }


    //위치 및 사진 정보 서버로 전송
    fun logFrameLocation(file: File, logType: LogType){
        val location = location.value!!
        val altitude = altitude.value?:location.altitude.toFloat()
        val videoId = if(logType == LogType.SINGLE_FRAME) SINGLE_IMAGE else userManager.getRecordingVideoID()
        val disposable = repository.logFrameLocation(
                userManager.getUserId(),
                userManager.getUserName(),
                videoId,
                location.longitude,
                location.latitude,
                altitude,
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
    fun onVideoStopped(file:File){
        onProgress.postValue(true)

        val location = location.value!!
        val altitude = altitude.value?:location.altitude.toFloat()
        val videoPartFile = ProgressRequestBody(file)
        observeFileUpload(videoPartFile)

        val videoRecord = VideoRecord(
            userManager.getUserId(),
            userManager.getUserName(),
            userManager.getRecordingVideoID(),
            location.longitude,
            location.latitude,
            altitude.toDouble(),
            videoPartFile
        )

        val disposable = repository.stopVideoLog(videoRecord)
                        .subscribe({
                            logAlert.value = LogAlert.SUCCESS
                            onProgress.value = false
                         }, {
                            logAlert.value = LogAlert.VIDEO_LOG_FAIL
                            onProgress.value = false
                            Log.i(TAG, "stopVideoLog: ${it.message}")
                        })
        mDisposable.add(disposable)
    }

    private fun observeFileUpload(videoPartFile : ProgressRequestBody){
        val fileDisposable =
                videoPartFile.getProgressSubject()
                        .subscribeOn(Schedulers.io())
                        .subscribe {
                            Log.i("PROGRESS", "stopVideoLog: $it")
                            progressValue.postValue(it)
                        }
        mDisposable.add(fileDisposable)
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



    fun locationServiceUnavailable() : Boolean{
        if(location.value == null){
            logAlert.value = LogAlert.LOCATION_UNAVAILABLE
            return true
        }
        return false
    }

    fun onUpload() : Boolean{
        if(onProgress.value == true){
            logAlert.value = LogAlert.ON_PROGRESS
            return true
        }
        return false
    }

    fun logout(){
        sharedPreferencesManager.setLogout()
        logout.value = Unit
    }


    override fun onCleared() {
        mDisposable.clear()
        super.onCleared()
    }

    companion object{
        const val SINGLE_IMAGE = 0
        private const val TAG = "MainViewModel"
    }

}

enum class LogAlert {
    SUCCESS,FAIL,FRAME_LOG_FAIL,VIDEO_LOG_FAIL,LOCATION_UNAVAILABLE,ON_PROGRESS
}

enum class LogType {
    SINGLE_FRAME, VIDEO_FRAME
}
