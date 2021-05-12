package com.drimase.datacollector.di.module

import com.drimase.datacollector.di.util.ActivityScope
import com.drimase.datacollector.ui.SplashActivity
import com.drimase.datacollector.ui.main.MainActivity
import com.tbruyelle.rxpermissions3.RxPermissions
import dagger.Module
import dagger.Provides

@Module
object SplashModule {

    @Provides
    @ActivityScope
    fun provideRxPermission(activity: SplashActivity): RxPermissions {
        return RxPermissions(activity)
    }
}