package com.drimase.datacollector.ui.registration

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drimase.datacollector.Repository
import com.drimase.datacollector.service.UserManager
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

private const val TAG = "RegistrationViewModel"

class RegistrationViewModel @Inject constructor(
    private val userManager: UserManager,
    private val repository: Repository
) : ViewModel(){
    val result = MutableLiveData<RegistrationResult>()
    private val mDisposable = CompositeDisposable()

    override fun onCleared() {
        mDisposable.clear()
        super.onCleared()
    }

    fun registration(loginId: String, password: String, repeatPassword: String){
        if(loginId.isBlank() || password.isBlank() || repeatPassword.isBlank()) {
            result.value = RegistrationResult.BLANK
            return
        }

        if(password != repeatPassword){
            result.value = RegistrationResult.PASSWORD_DIFFERENT
            return
        }

        val disposable = repository.registration(loginId,password)
            .subscribe({
                result.value = RegistrationResult.SUCCESS
                userManager.setUser(it)
            }, {
                Log.d(TAG, "registration: ${it.message}")
                result.value = RegistrationResult.SERVER_ERROR
            })
        mDisposable.add(disposable)
    }

}

enum class RegistrationResult{
    SUCCESS,BLANK, PASSWORD_DIFFERENT, SERVER_ERROR
}