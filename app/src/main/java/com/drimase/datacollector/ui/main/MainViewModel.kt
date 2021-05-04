package com.drimase.datacollector.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.camera.core.*
import androidx.lifecycle.*
import com.drimase.datacollector.GpsService
import com.drimase.datacollector.Repository
import com.drimase.datacollector.network.LogService
import com.tbruyelle.rxpermissions3.RxPermissions
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.io.File
import javax.inject.Inject

private const val TAG = "MainViewModel"

@SuppressLint("RestrictedApi")
class MainViewModel @Inject constructor(
        application: Application,
        private val repository: Repository
) : AndroidViewModel(application) {

    @Inject
    lateinit var gpsService: GpsService

    @Inject
    lateinit var dirs : File

    val liveText = MutableLiveData<String>("녹화")

    private var recording: Boolean = false

    private val mDisposable = CompositeDisposable()

    override fun onCleared() {
        mDisposable.clear()
        super.onCleared()
    }

    @SuppressLint("RestrictedApi")
    fun onRecordBtnClick(videoCapture :VideoCapture) {
        if (recording) {
            liveText.value = "녹화"
            videoCapture.stopRecording()
            Log.i(TAG, "Stop RECORDING")
        } else {
            liveText.value = "저장"
            val file = File(dirs,"${System.currentTimeMillis()}.mp4")

            videoCapture.startRecording(file, object : VideoCapture.OnVideoSavedListener {
                override fun onVideoSaved(file: File?) {
                    Log.i(TAG, "Video File : $file")
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
        recording = !recording
    }


    fun grantCheck(rxPermissions: RxPermissions): Boolean {
        for (p in permissions)
            if (!rxPermissions.isGranted(p))
                return false
        return true
    }

    companion object {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
        )
    }
}