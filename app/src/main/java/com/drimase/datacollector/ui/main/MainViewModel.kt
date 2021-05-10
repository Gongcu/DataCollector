package com.drimase.datacollector.ui.main

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
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.io.File
import javax.inject.Inject

private const val TAG = "MainViewModel"

@SuppressLint("RestrictedApi")
class MainViewModel @Inject constructor(
        application: Application,
        private val repository: Repository,
) : AndroidViewModel(application) {

    @Inject
    lateinit var userManager: UserManager

    @Inject
    lateinit var gpsService: GpsService

    @Inject
    lateinit var dirs : File

    val logAlert = MutableLiveData<LogAlert>()

    private val location : MutableLiveData<Location> by lazy{
        gpsService.locationLiveData
    }

    val accidentProneArea  = MutableLiveData<List<AccidentProneArea>>()
    val alert = MutableLiveData<Unit>()
    private val detectedAPA = ArrayList<AccidentProneArea>()
    private val mDisposable = CompositeDisposable()

    override fun onCleared() {
        mDisposable.clear()
        super.onCleared()
    }

    fun takePhoto(imageCapture: ImageCapture){
        val file = File(dirs,"${System.currentTimeMillis()}.jpeg")

        imageCapture.takePicture(file, object : ImageCapture.OnImageSavedListener {
            override fun onImageSaved(file: File) {
                Log.i(TAG, "Image File : $file")
                val location = gpsService.locationLiveData.value!!
                repository.logImageLocation(
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


    private fun checkAlerted(list : List<AccidentProneArea>) : Boolean{
        for(area in list){
            if(!detectedAPA.contains(area)){
                detectedAPA.add(area)
                return true
            }
        }
        return false
    }
}

enum class LogAlert {
    SUCCESS,FAIL
}