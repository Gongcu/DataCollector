package com.drimase.datacollector.di.module

import android.app.Application
import android.content.Context
import android.location.Location
import androidx.lifecycle.MutableLiveData
import com.drimase.datacollector.BaseApplication
import com.drimase.datacollector.R
import com.drimase.datacollector.di.util.ActivityScope
import com.drimase.datacollector.service.UserManager
import com.drimase.datacollector.di.util.ApplicationContext
import com.drimase.datacollector.service.CustomLocationListener
import dagger.Module
import dagger.Provides
import java.io.File
import javax.inject.Singleton

@Module(includes = [
    ViewModelModule::class,
    NetworkModule::class
]) //ViewModelModule을 App 범위로 관리 -> 어디서든 ViewModelFactory로 ViewModel 생성가능
class AppModule {
    private val location = MutableLiveData<Location>()

    @Provides
    @Singleton
    fun provideApp(application: BaseApplication) : Application{
        return application
    }

    @Provides
    @Singleton
    @ApplicationContext
    fun provideContext(application: BaseApplication) : Context {
        return application
    }

    @Singleton
    @Provides
    fun provideUserManager(): UserManager {
        return UserManager()
    }

    @Singleton
    @Provides
    fun provideLocationLiveData() : MutableLiveData<Location> {
        return location
    }

    @Provides
    fun provideDirs(application: BaseApplication) : File {
        val mediaDir = application.externalMediaDirs.firstOrNull()?.let{
            File(it,application.resources.getString(R.string.app_name)).apply {
                mkdirs()
            }
        }
        return mediaDir!!
    }

}