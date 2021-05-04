package com.drimase.datacollector.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.camera.core.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.drimase.datacollector.BaseActivity
import com.drimase.datacollector.R
import com.drimase.datacollector.databinding.ActivityMainBinding
import com.drimase.datacollector.di.ViewModelFactory
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : BaseActivity(),LifecycleOwner {

    @Inject
    lateinit var rxPermissions : RxPermissions



    private lateinit var videoCaptureConfig: VideoCaptureConfig

    private lateinit var videoCapture: VideoCapture

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
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(
            this,
                R.layout.activity_main
        )
        binding.viewModel = mainViewModel
        binding.lifecycleOwner = this

        requestPermission()

        record_button.setOnClickListener {
            view_finder.post {
                mainViewModel.onRecordBtnClick(videoCapture)
            }
        }
        mainViewModel.liveText.observe(this, {
            record_button.text = it
        })

        mainViewModel.gpsService.locationLiveData.observe(this,{
            latitude_text_view.text = it?.latitude.toString()
            longitude_text_view.text = it?.longitude.toString()
        })
    }


    @SuppressLint("RestrictedApi")
    private fun startCamera() {
        val previewConfig = PreviewConfig.Builder().build()
        val preview = Preview(previewConfig)

        preview.setOnPreviewOutputUpdateListener {
            view_finder.setSurfaceTexture(it.surfaceTexture)
        }

        videoCaptureConfig= VideoCaptureConfig.Builder()
            .setLensFacing(CameraX.LensFacing.BACK)
            .build()
        videoCapture = VideoCapture(videoCaptureConfig)

        CameraX.bindToLifecycle(this, preview, videoCapture)
    }

    private fun requestPermission() {
        if (mainViewModel.grantCheck(rxPermissions))
            startCamera()
        else
            rxPermissions
                .request(*MainViewModel.permissions).subscribe { grant ->
                    if (!grant)
                        Toast.makeText(
                            MainActivity::class.java as Context,
                            "앱 설정에서 전체 권한을 허용해주세요.",
                            Toast.LENGTH_LONG
                        )
                    startCamera()
                }
    }
}