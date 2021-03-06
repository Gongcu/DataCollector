package com.drimase.datacollector.di.module

import com.drimase.datacollector.service.LogService
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


private const val BASE_URL = "IP:PORT"

@Module
object NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        /*
        val clientBuilder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = clientBuilder.addInterceptor(loggingInterceptor).build()
        */

        return Retrofit.Builder().baseUrl(BASE_URL)
                //.client(client)
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    @Provides
    @Singleton
    fun provideRetrofitService(retrofit: Retrofit) : LogService {
        return retrofit.create(LogService::class.java)
    }
}
