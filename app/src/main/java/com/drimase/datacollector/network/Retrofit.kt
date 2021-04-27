package com.drimase.datacollector.network

import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

private const val URL = "http://"

object Retrofit {
    private var instance : Retrofit? = null

    fun getInstance():Retrofit {
        if (instance == null)
            instance = Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
        return instance!!
    }
}