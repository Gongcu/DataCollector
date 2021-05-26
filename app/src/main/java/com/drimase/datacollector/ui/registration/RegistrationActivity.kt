package com.drimase.datacollector.ui.registration

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.drimase.datacollector.base.BaseActivity
import com.drimase.datacollector.R
import com.drimase.datacollector.databinding.ActivityRegistrationBinding
import com.drimase.datacollector.di.ViewModelFactory
import com.drimase.datacollector.ui.login.LoginActivity
import javax.inject.Inject

class RegistrationActivity : BaseActivity<ActivityRegistrationBinding>() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel : RegistrationViewModel by lazy {
        ViewModelProvider(this@RegistrationActivity, viewModelFactory).get(RegistrationViewModel::class.java)
    }

    override fun layoutRes(): Int {
        return R.layout.activity_registration
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getViewDataBinding().viewModel = viewModel


        viewModel.result.observe(this,{
            when(it){
                RegistrationResult.SUCCESS -> goToLoginActivity()
                RegistrationResult.SERVER_ERROR -> Toast.makeText(this,"서버 오류 발생 : 관리자 문의 필요",Toast.LENGTH_LONG).show()
                RegistrationResult.BLANK -> Toast.makeText(this,"내용을 모두 입력해주세요",Toast.LENGTH_LONG).show()
                RegistrationResult.PASSWORD_DIFFERENT -> Toast.makeText(this,"입력한 비밀번호가 서로 다릅니다.",Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun goToLoginActivity(){
        val intent = Intent(this,LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }
}