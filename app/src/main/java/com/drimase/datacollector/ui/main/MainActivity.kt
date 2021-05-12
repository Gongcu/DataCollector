package com.drimase.datacollector.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.camera.core.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.drimase.datacollector.BaseActivity
import com.drimase.datacollector.R
import com.drimase.datacollector.databinding.ActivityMainBinding
import com.drimase.datacollector.di.ViewModelFactory
import com.drimase.datacollector.ui.login.LoginViewModel
import com.tbruyelle.rxpermissions3.RxPermissions
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

        mainViewModel.gpsService.locationLiveData.observe(this,{
            //latitude_text_view.text = it?.latitude.toString()
            //longitude_text_view.text = it?.longitude.toString()
            mainViewModel.detect()
        })

        mainViewModel.alert.observe(this,{
            generateSound()
            generateToast()
        })


        mainViewModel.progress.observe(this,{
            progressbar.progress = it.toInt()
        })

        mainViewModel.networking.observe(this,{
            if(it){
                progressbar.visibility= View.VISIBLE
            }else{
                progressbar.visibility= View.INVISIBLE
            }
        })

        mainViewModel.logAlert.observe(this,{
            when(it){
                LogAlert.SUCCESS -> Toast.makeText(this,"기록 성공",Toast.LENGTH_SHORT).show()
                LogAlert.FAIL -> Toast.makeText(this,"기록 실패",Toast.LENGTH_SHORT).show()
            }
        })
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
}