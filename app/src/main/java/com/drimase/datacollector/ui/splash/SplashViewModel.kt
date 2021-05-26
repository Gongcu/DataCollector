package com.drimase.datacollector.ui.splash

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.drimase.datacollector.dto.User
import com.drimase.datacollector.service.SharedPreferencesManager
import com.drimase.datacollector.service.UserManager
import com.tbruyelle.rxpermissions3.RxPermissions
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    private val userManager: UserManager,
    private val sharedPreferencesManager: SharedPreferencesManager,
    application: Application
) : AndroidViewModel(application){

    val grantStatus = MutableLiveData<GrantStatus>()


    fun getUserInfo(): Boolean{
        val userId = sharedPreferencesManager.getUserId()
        val userName = sharedPreferencesManager.getUserName()
        setUserInfo(userId,userName)
        return userId!=-1
    }

    private fun setUserInfo(userId: Int, userName:String){
        userManager.setUser(User(userId,userName,""))
    }


    @SuppressLint("ShowToast")
    fun requestPermission(rxPermissions: RxPermissions) {
        if(!grantCheck(rxPermissions))
            rxPermissions
                .requestEachCombined(*permissions)
                .subscribe { permission ->
                    if (permission.granted) {
                        grantStatus.postValue(GrantStatus.GRANTED)
                    }else{
                        grantStatus.postValue(GrantStatus.FAIL)
                    }
                }
        else{
            grantStatus.postValue(GrantStatus.GRANTED)
        }
    }

    private fun grantCheck(rxPermissions: RxPermissions): Boolean {
        for (p in permissions)
            if (!rxPermissions.isGranted(p))
                return false
        return true
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

    enum class GrantStatus{
        GRANTED,FAIL
    }
}