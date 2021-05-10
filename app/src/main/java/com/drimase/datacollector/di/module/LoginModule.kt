package com.drimase.datacollector.di.module

import com.drimase.datacollector.di.util.ActivityScope
import com.drimase.datacollector.ui.login.LoginActivity
import com.drimase.datacollector.ui.main.MainActivity
import com.tbruyelle.rxpermissions3.RxPermissions
import dagger.Module
import dagger.Provides

@Module
object LoginModule {
    @Provides
    @ActivityScope
    fun provideRxPermission(activity: LoginActivity): RxPermissions {
        return RxPermissions(activity)
    }
}