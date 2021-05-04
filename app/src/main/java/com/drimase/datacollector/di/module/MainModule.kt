package com.drimase.datacollector.di.module

import android.content.Context
import com.drimase.datacollector.BaseApplication
import com.drimase.datacollector.GpsService
import com.drimase.datacollector.ui.main.MainActivity
import com.drimase.datacollector.di.util.ActivityContext
import com.drimase.datacollector.di.util.ActivityScope
import com.tbruyelle.rxpermissions3.RxPermissions
import dagger.Module
import dagger.Provides

@Module
object MainModule {

    @Provides
    @ActivityContext
    @JvmStatic
    fun provideContext(activity: MainActivity) : Context {
        return activity
    }



    /**
     * Permission
     */
    @Provides
    @ActivityScope
    fun provideRxPermission(activity: MainActivity): RxPermissions {
        return RxPermissions(activity)
    }

    /**
     * LOCATION SERVICE
     */
    @Provides
    @ActivityScope
    fun provideGpsService(application: BaseApplication) : GpsService{
        return GpsService(application)
    }
}