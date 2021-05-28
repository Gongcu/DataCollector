package com.drimase.datacollector.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.camera.core.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.drimase.datacollector.base.BaseActivity
import com.drimase.datacollector.R
import com.drimase.datacollector.databinding.ActivityMainBinding
import com.drimase.datacollector.di.ViewModelFactory
import com.drimase.datacollector.ui.login.LoginActivity
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity(),LifecycleOwner {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val mainViewModel :MainViewModel by lazy {
        ViewModelProvider(this@MainActivity, viewModelFactory).get(MainViewModel::class.java)
    }

    private lateinit var dataBinding :ActivityMainBinding

    @Inject
    lateinit var dirs : File

    private lateinit var imageCapture: ImageCapture
    private lateinit var videoCapture: VideoCapture

    private var recording : Boolean = false

    private val locationObserver = Observer<Location> {
        if (recording && mainViewModel.userManager.getRecordingVideoID() > 0) {
            captureVideoFrame()
        }
    }

    val recordText = MutableLiveData("녹화")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        dataBinding.lifecycleOwner = this
        dataBinding.viewModel = mainViewModel
        dataBinding.activity = this

        startCamera()


        mainViewModel.location.observe(this,{
            mainViewModel.detectAccidentProneArea()
        })

        mainViewModel.accidentProneAreaAlert.observe(this,{
            generateSound()
            generateToast()
        })


        //Networking
        mainViewModel.progressValue.observe(this,{
            progressbar.setProgress(it.toInt(),true)
        })

        mainViewModel.onProgress.observe(this,{
            if(it)
                progressbar.visibility= View.VISIBLE
            else
                progressbar.visibility= View.INVISIBLE
        })

        //Log Status
        mainViewModel.logAlert.observe(this,{
            when(it){
                LogAlert.SUCCESS -> Toast.makeText(this,"기록 성공",Toast.LENGTH_SHORT).show()
                LogAlert.FAIL -> Toast.makeText(this,"기록 실패",Toast.LENGTH_SHORT).show()
                LogAlert.LOCATION_UNAVAILABLE -> Toast.makeText(this,"위치 정보를 가져올 수 없습니다",Toast.LENGTH_SHORT).show()
                LogAlert.ON_PROGRESS -> Toast.makeText(this,"현재 영상을 업로드 중입니다.",Toast.LENGTH_SHORT).show()
            }
        })

        //Logout
        mainViewModel.logout.observe(this,{
            goToLoginActivity()
        })

    }

    override fun onStop() {
        super.onStop()
        if(mainViewModel.recording) {
            stopVideoLog()
        }
    }


    @SuppressLint("RestrictedApi")
    private fun startCamera() {
        val previewConfig = PreviewConfig.Builder().build()
        val preview = Preview(previewConfig)

        preview.setOnPreviewOutputUpdateListener {
            view_finder.setSurfaceTexture(it.surfaceTexture)
        }

        setImageCapture()
        setVideoCapture()

        CameraX.bindToLifecycle(this, preview, imageCapture, videoCapture)
    }



    private fun setImageCapture(){
        val imageCaptureConfig= ImageCaptureConfig.Builder()
                .setLensFacing(CameraX.LensFacing.BACK)
                .build()

        imageCapture = ImageCapture(imageCaptureConfig)
    }

    @SuppressLint("RestrictedApi")
    private fun setVideoCapture(){
        val videoCaptureConfig= VideoCaptureConfig.Builder()
                .setLensFacing(CameraX.LensFacing.BACK)
                .build()

        videoCapture = VideoCapture(videoCaptureConfig)
    }


    @SuppressLint("RestrictedApi")
    fun onRecordClick(){
        if(recording){
            stopVideoLog()
        }else{
            if(mainViewModel.locationServiceUnavailable() || mainViewModel.onUpload())
                return
            startVideoLog()
        }
    }

    @SuppressLint("RestrictedApi")
    private fun startVideoLog(){
        recording=!recording
        recordText.value = "중지"
        mainViewModel.setRecordingVideoIdToUserManager()
        mainViewModel.location.observe(this, locationObserver)
        val file = File(dirs,"${System.currentTimeMillis()}.mp4")
        videoCapture.startRecording(file, object : VideoCapture.OnVideoSavedListener {
            override fun onVideoSaved(file: File) {
                Log.i(TAG, "Video File : $file")
                mainViewModel.onVideoStopped(file)
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

    @SuppressLint("RestrictedApi")
    private fun stopVideoLog(){
        mainViewModel.location.removeObserver(locationObserver)
        recordText.value = "녹화"
        videoCapture.stopRecording()
        recording=!recording
    }

    //영상 프레임 캡쳐
    private fun captureVideoFrame(){
        val file = File(dirs,"${System.currentTimeMillis()}.jpeg")

        imageCapture.takePicture(file, object : ImageCapture.OnImageSavedListener {
            override fun onImageSaved(file: File) {
                Log.i(TAG, "Image File : $file")
                mainViewModel.logFrameLocation(file,LogType.VIDEO_FRAME)
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

    //단일 사진 촬영
    fun takePhoto(){
        if(mainViewModel.locationServiceUnavailable() || mainViewModel.onUpload())
            return

        val file = File(dirs,"${System.currentTimeMillis()}.jpeg")
        imageCapture.takePicture(file, object : ImageCapture.OnImageSavedListener {
            override fun onImageSaved(file: File) {
                mainViewModel.logFrameLocation(file,LogType.SINGLE_FRAME)
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

    private fun generateSound(){
        val mediaPlayer: MediaPlayer = MediaPlayer.create(applicationContext, R.raw.alert)
        mediaPlayer.start()
        Log.d("ALERT", "generateSound")
    }
    private fun generateToast(){
        Toast.makeText(applicationContext,"사고 다발 지역입니다.",Toast.LENGTH_LONG).show()
    }

    private fun goToLoginActivity(){
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }


    companion object{
        private const val TAG = "MainActivity"
    }

}