package com.drimase.datacollector.di.module

import android.content.Context
import android.media.MediaPlayer
import androidx.fragment.app.FragmentActivity
import com.drimase.datacollector.BaseApplication
import com.drimase.datacollector.GpsService
import com.drimase.datacollector.R
import com.drimase.datacollector.ui.main.MainActivity
import com.drimase.datacollector.di.util.ActivityContext
import com.drimase.datacollector.di.util.ActivityScope
import com.drimase.datacollector.ui.login.LoginActivity
import com.tbruyelle.rxpermissions3.RxPermissions
import dagger.Module
import dagger.Provides
import java.io.File

@Module
object MainModule {

    @Provides
    @ActivityContext
    @JvmStatic
    fun provideActivity(activity: MainActivity) : MainActivity {
        return activity
    }

    @Provides
    @ActivityContext
    fun provideContext(activity: MainActivity) : Context {
        return activity
    }





    @Provides
    @ActivityScope
    fun provideGpsService(application: BaseApplication) : GpsService{
        return GpsService(application)
    }
}