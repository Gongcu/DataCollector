package com.drimase.datacollector

import com.drimase.datacollector.dto.Location
import com.drimase.datacollector.dto.User
import com.drimase.datacollector.network.LogService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

class Repository @Inject constructor(
    private val logService: LogService
){

    fun registration(loginId:String, password:String) : Single<User> {
        return logService.registration(loginId, password)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    fun login(loginId:String, password:String): Single<User>{
        return logService.login(loginId, password)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    /*
    fun logLocation(userId: Int, location:Location): Single<Void>{
        return logService.logLocation(userId,location)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    fun uploadVideo(userId:Int,videoId:Int, file: File): Single<Void>{
        val requestFile = RequestBody.create(MediaType.parse("multipart/from-data"), file)
        val videoId = RequestBody.create(MediaType.parse("multipart/from-data"), videoId.toString())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        return logService.uploadVideo(userId,videoId,body)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }*/
}