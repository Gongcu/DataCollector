package com.drimase.datacollector.ui.login

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.drimase.datacollector.BaseActivity
import com.drimase.datacollector.R
import com.drimase.datacollector.databinding.ActivityLoginBinding
import com.drimase.datacollector.di.ViewModelFactory
import com.drimase.datacollector.ui.main.MainActivity
import com.drimase.datacollector.ui.registration.RegistrationActivity
import com.tbruyelle.rxpermissions3.RxPermissions
import javax.inject.Inject

private const val TAG = "LoginActivity"

class LoginActivity : BaseActivity<ActivityLoginBinding>() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var rxPermissions: RxPermissions

    private val viewModel: LoginViewModel by lazy {
        ViewModelProvider(this@LoginActivity, viewModelFactory).get(LoginViewModel::class.java)
    }

    override fun layoutRes(): Int {
        return R.layout.activity_login
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.getViewDataBinding().viewModel = viewModel

        requestPermission()

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
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun requestPermission() {
        if(!viewModel.grantCheck(rxPermissions))
            rxPermissions
                    .request(*LoginViewModel.permissions).subscribe { grant ->
                        if (!grant) {
                            Toast.makeText(
                                    MainActivity::class.java as Context,
                                    "전체 권한을 허용해주세요.",
                                    Toast.LENGTH_LONG
                            )
                            requestPermission()
                        }
                    }
    }

    companion object {
        const val ADMIN_LOGIN_COUNT = 3
    }
}