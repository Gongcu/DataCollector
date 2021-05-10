package com.drimase.datacollector.ui.login

import android.Manifest
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drimase.datacollector.R
import com.drimase.datacollector.Repository
import com.drimase.datacollector.UserManager
import com.tbruyelle.rxpermissions3.RxPermissions
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

private const val TAG = "LoginViewModel"

class LoginViewModel @Inject constructor(
    private val userManager: UserManager,
    private val repository: Repository
) :ViewModel(){
    val login = MutableLiveData<LoginResult>()
    private var adminCounter = 0
    private val mDisposable = CompositeDisposable()

    override fun onCleared() {
        mDisposable.clear()
        super.onCleared()
    }

    fun addCount(){
        adminCounter+=1
        if(adminCounter == 3) {
            repository.adminLogin().subscribe({
                userManager.setUser(it)
                login.value = LoginResult.ADMIN_LOGIN_SUCCESS
            }, {
                login.value = LoginResult.ADMIN_LOGIN_FAIL
                Log.d(TAG, "ADMIN_LOGIN_FAIL: ${it.message}")
            })
            adminCounter = 0
        }
    }

    fun login(loginId: String, password:String){
        repository.login(loginId,password)
            .subscribe({
                userManager.setUser(it)
                login.value = LoginResult.SUCCESS
            }, {
                login.value = LoginResult.NOT_MATCH
                Log.d(TAG, "login: ${it.message}")
            })
    }

    fun grantCheck(rxPermissions: RxPermissions): Boolean {
        for (p in permissions)
            if (!rxPermissions.isGranted(p))
                return false
        return true
    }

    fun goToRegistration(){
        login.value = LoginResult.REGISTRATION
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

enum class LoginResult{
    SUCCESS,NOT_MATCH,REGISTRATION, ADMIN_LOGIN_SUCCESS, ADMIN_LOGIN_FAIL
}