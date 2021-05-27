package com.drimase.datacollector.ui.splash

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.drimase.datacollector.R
import com.drimase.datacollector.base.BaseApplication
import com.drimase.datacollector.di.ViewModelFactory
import com.drimase.datacollector.ui.main.MainActivity
import com.drimase.datacollector.ui.registration.RegistrationActivity
import com.google.android.material.snackbar.Snackbar
import com.tbruyelle.rxpermissions3.RxPermissions
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class SplashActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var rxPermissions: RxPermissions
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel : SplashViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(SplashViewModel::class.java)
    }

    private var locationAvailable : Boolean = false

    private val requestActivity: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
    ) {  }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        locationAvailable = viewModel.isLocationEnabled()

        if(!locationAvailable){
            goToLocationSettings()
            (application as BaseApplication).makeToast("상단의 위치 권한을 사용으로 변경하세요.")
        }

        viewModel.requestPermission(rxPermissions)

        viewModel.grantStatus.observe(this,{
            when(it) {
                SplashViewModel.GrantStatus.GRANTED -> {
                        goNextActivity()
                }
                SplashViewModel.GrantStatus.FAIL -> {
                    goToApplicationSettings()
                    (application as BaseApplication).makeToast("앱 설정에서 권한을 허용해야 사용할 수 있습니다.")
                }
            }
        })

    }

    private fun goToLocationSettings(){
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        requestActivity.launch(intent)
    }
    private fun goToApplicationSettings(){
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package",packageName,null)
        intent.data = uri
        requestActivity.launch(intent)
    }

    override fun onBackPressed() {
        //뒤로가기 동작 안함
    }


    private fun goNextActivity(){
        val userInfo = viewModel.getUserInfo()
        val intent : Intent =
            if(userInfo){
                Intent(this, MainActivity::class.java)
            }else{
                Intent(this, RegistrationActivity::class.java)
            }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

}