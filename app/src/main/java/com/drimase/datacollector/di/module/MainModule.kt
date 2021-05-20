package com.drimase.datacollector.di.module

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.drimase.datacollector.BaseApplication
import com.drimase.datacollector.service.GpsService
import com.drimase.datacollector.ui.main.MainActivity
import com.drimase.datacollector.di.util.ActivityContext
import com.drimase.datacollector.di.util.ActivityScope
import dagger.Module
import dagger.Provides

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
    fun provideGpsService(application: BaseApplication, location: MutableLiveData<Location>) : GpsService {
        return GpsService(application,location)
    }
}