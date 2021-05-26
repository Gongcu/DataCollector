package com.drimase.datacollector.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.drimase.datacollector.base.BaseActivity
import com.drimase.datacollector.R
import com.drimase.datacollector.databinding.ActivityLoginBinding
import com.drimase.datacollector.di.ViewModelFactory
import com.drimase.datacollector.ui.main.MainActivity
import com.drimase.datacollector.ui.registration.RegistrationActivity
import javax.inject.Inject

class LoginActivity : BaseActivity<ActivityLoginBinding>() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: LoginViewModel by lazy {
        ViewModelProvider(this@LoginActivity, viewModelFactory).get(LoginViewModel::class.java)
    }

    override fun layoutRes(): Int {
        return R.layout.activity_login
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.getViewDataBinding().viewModel = viewModel

        viewModel.login.observe(this, {
            when (it) {
                LoginResult.SUCCESS -> startActivity(MainActivity::class.java as Class<Activity>)
                LoginResult.ADMIN_LOGIN_SUCCESS -> startActivity(MainActivity::class.java as Class<Activity>)
                LoginResult.REGISTRATION -> startActivity(RegistrationActivity::class.java as Class<Activity>)
                LoginResult.NOT_MATCH -> Toast.makeText(this, "일치하는 계정이 없습니다", Toast.LENGTH_LONG).show()
                LoginResult.ADMIN_LOGIN_FAIL -> Toast.makeText(this, "관리자 계정 접속 불가", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun startActivity(activity: Class<Activity>) {
        val intent = Intent(this, activity)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    companion object {
        const val ADMIN_LOGIN_COUNT = 3
    }
}