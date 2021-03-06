package com.drimase.datacollector.di.module

import android.content.Context
import android.location.Location
import androidx.lifecycle.MutableLiveData
import com.drimase.datacollector.base.BaseApplication
import com.drimase.datacollector.ui.main.MainActivity
import com.drimase.datacollector.di.util.ActivityContext
import com.drimase.datacollector.di.util.ActivityScope
import com.drimase.datacollector.service.LocationService
import com.drimase.datacollector.service.SensorManager
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
    fun provideGpsService(application: BaseApplication, location:MutableLiveData<Location>) : LocationService {
        return LocationService(application,location)
    }

    @Provides
    @ActivityScope
    fun provideSensorService(application: BaseApplication, altitude:MutableLiveData<Float>) : SensorManager{
        return SensorManager(application,altitude)
    }
}