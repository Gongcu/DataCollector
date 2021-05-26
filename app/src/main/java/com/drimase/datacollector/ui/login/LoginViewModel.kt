package com.drimase.datacollector.ui.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drimase.datacollector.repository.Repository
import com.drimase.datacollector.service.SharedPreferencesManager
import com.drimase.datacollector.service.UserManager
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

private const val TAG = "LoginViewModel"

class LoginViewModel @Inject constructor(
    private val sharedPreferencesManager: SharedPreferencesManager,
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

    //HIDDEN ADMIN LOGIN
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
                sharedPreferencesManager.setUserId(it.id)
                sharedPreferencesManager.setUserName(it.loginId)
                login.value = LoginResult.SUCCESS
            }, {
                login.value = LoginResult.NOT_MATCH
                Log.d(TAG, "login: ${it.message}")
            })
    }


    fun goToRegistration(){
        login.value = LoginResult.REGISTRATION
    }

}

enum class LoginResult{
    SUCCESS,NOT_MATCH,REGISTRATION, ADMIN_LOGIN_SUCCESS, ADMIN_LOGIN_FAIL
}