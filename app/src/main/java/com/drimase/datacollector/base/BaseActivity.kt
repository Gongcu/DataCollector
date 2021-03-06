package com.drimase.datacollector.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.annotation.Nullable
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import dagger.android.support.DaggerAppCompatActivity


abstract class BaseActivity<T : ViewDataBinding>() : DaggerAppCompatActivity(){

    private lateinit var mViewDataBinding: T


    fun getViewDataBinding() : T{
        return mViewDataBinding
    }


    @LayoutRes
    protected abstract fun layoutRes(): Int


    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding()
    }

    private fun dataBinding(){
        mViewDataBinding = DataBindingUtil.inflate<T>(layoutInflater, layoutRes(), null, false)
        mViewDataBinding.lifecycleOwner = this
        setContentView(mViewDataBinding.root)
    }
}