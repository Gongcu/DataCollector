package com.drimase.datacollector.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.camera.core.*
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.drimase.datacollector.base.BaseActivity
import com.drimase.datacollector.R
import com.drimase.datacollector.databinding.ActivityMainBinding
import com.drimase.datacollector.di.ViewModelFactory
import com.drimase.datacollector.ui.login.LoginActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : BaseActivity<ActivityMainBinding>(),LifecycleOwner {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val mainViewModel :MainViewModel by lazy {
        ViewModelProvider(this@MainActivity, viewModelFactory).get(MainViewModel::class.java)
    }

    override fun layoutRes(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getViewDataBinding().viewModel = mainViewModel

        startCamera()

        mainViewModel.location.observe(this,{
            mainViewModel.detect()
        })

        mainViewModel.alert.observe(this,{
            generateSound()
            generateToast()
        })


        mainViewModel.progressValue.observe(this,{
            progressbar.setProgress(it.toInt(),true)
        })

        mainViewModel.onProgress.observe(this,{
            if(it)
                progressbar.visibility= View.VISIBLE
            else
                progressbar.visibility= View.INVISIBLE
        })

        mainViewModel.logAlert.observe(this,{
            when(it){
                LogAlert.SUCCESS -> Toast.makeText(this,"기록 성공",Toast.LENGTH_SHORT).show()
                LogAlert.FAIL -> Toast.makeText(this,"기록 실패",Toast.LENGTH_SHORT).show()
                LogAlert.LOCATION_UNAVAILABLE -> Toast.makeText(this,"위치 정보를 가져올 수 없습니다",Toast.LENGTH_SHORT).show()
                LogAlert.ON_PROGRESS -> Toast.makeText(this,"현재 영상을 업로드 중입니다.",Toast.LENGTH_SHORT).show()
            }
        })

        mainViewModel.logout.observe(this,{
            goToLoginActivity()
        })

    }

    override fun onStop() {
        super.onStop()
        if(mainViewModel.recording) {
            mainViewModel.stopVideoLog()
        }
    }


    @SuppressLint("RestrictedApi")
    private fun startCamera() {
        val previewConfig = PreviewConfig.Builder().build()
        val preview = Preview(previewConfig)

        preview.setOnPreviewOutputUpdateListener {
            view_finder.setSurfaceTexture(it.surfaceTexture)
        }

        mainViewModel.setImageCapture()
        mainViewModel.setVideoCapture()

        CameraX.bindToLifecycle(this, preview, mainViewModel.imageCapture, mainViewModel.videoCapture)
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
}