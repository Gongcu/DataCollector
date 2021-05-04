package com.drimase.datacollector.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drimase.datacollector.BaseActivity
import com.drimase.datacollector.R
import com.drimase.datacollector.databinding.ActivityLoginBinding
import com.drimase.datacollector.databinding.ActivityRegistrationBinding
import com.drimase.datacollector.di.ViewModelFactory
import com.drimase.datacollector.ui.main.MainActivity
import com.drimase.datacollector.ui.registration.RegistrationActivity
import javax.inject.Inject

class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding

    @Inject
    lateinit var viewModelFactory : ViewModelFactory

    private val viewModel : LoginViewModel by lazy{
        ViewModelProvider(this@LoginActivity,viewModelFactory).get(LoginViewModel::class.java)
    }

    override fun layoutRes(): Int {
        return R.layout.activity_login
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(layoutInflater,R.layout.activity_login,null,false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.login.observe(this,{
            when(it){
                LoginResult.SUCCESS -> Intent(this, MainActivity::class.java)
                LoginResult.NOT_MATCH -> Toast.makeText(this, "일치하는 계정이 없습니다",Toast.LENGTH_LONG).show()
                LoginResult.REGISTRATION -> Intent(this,RegistrationActivity::class.java)
            }
        })
    }
}