package com.drimase.datacollector.ui.registration

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.drimase.datacollector.BaseActivity
import com.drimase.datacollector.R
import com.drimase.datacollector.databinding.ActivityRegistrationBinding
import com.drimase.datacollector.di.ViewModelFactory
import com.drimase.datacollector.ui.login.LoginActivity
import com.drimase.datacollector.ui.main.MainViewModel
import javax.inject.Inject

class RegistrationActivity : BaseActivity<ActivityRegistrationBinding>() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel : RegistrationViewModel by lazy {
        ViewModelProvider(this@RegistrationActivity, viewModelFactory).get(RegistrationViewModel::class.java)
    }
    private lateinit var binding: ActivityRegistrationBinding

    override fun layoutRes(): Int {
        return R.layout.activity_registration
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getViewDataBinding().viewModel = viewModel


        viewModel.result.observe(this,{
            when(it){
                RegistrationResult.SUCCESS -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
                RegistrationResult.SERVER_ERROR -> Toast.makeText(this,"서버 오류 발생 : 관리자 문의 필요",Toast.LENGTH_LONG).show()
                RegistrationResult.BLANK -> Toast.makeText(this,"내용을 모두 입력해주세요",Toast.LENGTH_LONG).show()
                RegistrationResult.PASSWORD_DIFFERENT -> Toast.makeText(this,"입력한 비밀번호가 서로 다릅니다.",Toast.LENGTH_LONG).show()
            }
        })
    }
}