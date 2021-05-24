package com.drimase.datacollector.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.drimase.datacollector.R
import com.drimase.datacollector.ui.main.MainActivity
import com.tbruyelle.rxpermissions3.RxPermissions
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class SplashActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var rxPermissions: RxPermissions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        requestPermission(rxPermissions)

    }

    @SuppressLint("ShowToast")
    private fun requestPermission(rxPermissions: RxPermissions) {
        if(!grantCheck(rxPermissions))
            rxPermissions
                .requestEachCombined(*permissions)
                .subscribe { permission ->
                        if (permission.granted) {
                            goToMainActivity()
                        }else{
                            Toast.makeText(
                                this,
                                "앱 설정에서 전체 권한을 허용해야 앱을 이용할 수 있습니다.",
                                Toast.LENGTH_LONG
                            )
                        }
                    }
        else{
            goToMainActivity()
        }
    }
    private fun grantCheck(rxPermissions: RxPermissions): Boolean {
        for (p in permissions)
            if (!rxPermissions.isGranted(p))
                return false
        return true
    }

    private fun goToMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    companion object{
        val permissions = arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        )
    }
}