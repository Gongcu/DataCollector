package com.drimase.datacollector.ui.splash

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.drimase.datacollector.R
import com.drimase.datacollector.di.ViewModelFactory
import com.drimase.datacollector.ui.main.MainActivity
import com.drimase.datacollector.ui.registration.RegistrationActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        viewModel.requestPermission(rxPermissions)

        viewModel.grantStatus.observe(this,{
            when(it){
                SplashViewModel.GrantStatus.GRANTED -> goNextActivity()
                SplashViewModel.GrantStatus.FAIL ->
                    Toast.makeText(
                        this,
                        "앱 설정에서 전체 권한을 허용해야 앱을 이용할 수 있습니다.",
                        Toast.LENGTH_LONG
                    )
            }
        })

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