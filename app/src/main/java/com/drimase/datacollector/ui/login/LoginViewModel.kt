package com.drimase.datacollector.ui.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drimase.datacollector.R
import com.drimase.datacollector.Repository
import com.drimase.datacollector.UserManager
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

private const val TAG = "LoginViewModel"

class LoginViewModel @Inject constructor(
    private val userManager: UserManager,
    private val repository: Repository
) :ViewModel(){
    val login = MutableLiveData<LoginResult>()
    private val mDisposable = CompositeDisposable()

    override fun onCleared() {
        mDisposable.clear()
        super.onCleared()
    }

    fun login(loginId: String, password:String){
        repository.login(loginId,password)
            .subscribe({
                userManager.user = it
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
    SUCCESS,NOT_MATCH,REGISTRATION
}