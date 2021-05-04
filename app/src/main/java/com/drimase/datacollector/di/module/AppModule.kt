package com.drimase.datacollector.di.module

import android.app.Application
import android.content.Context
import com.drimase.datacollector.BaseApplication
import com.drimase.datacollector.UserManager
import com.drimase.datacollector.di.util.ApplicationContext
import com.drimase.datacollector.network.LogService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import javax.inject.Singleton

private const val BASE_URL = "http://123.123.123.123"
@Module(includes = [
    ViewModelModule::class]
) //ViewModelModule을 App 범위로 관리 -> 어디서든 ViewModelFactory로 ViewModel 생성가능
class AppModule {
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

    @Provides
    fun provideDirs(application: BaseApplication) : File {
        return application.externalMediaDirs.first()
    }

    @Singleton
    @Provides
    fun provideUserManager():UserManager{
        return UserManager()
    }


    /**
     * Network Module
     */
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofitService(retrofit: Retrofit) : LogService{
        return retrofit.create(LogService::class.java)
    }
}