package com.drimase.datacollector

import android.location.Location
import android.util.Log
import com.drimase.datacollector.dto.AccidentProneArea
import com.drimase.datacollector.dto.RegistrationRequest
import com.drimase.datacollector.dto.User
import com.drimase.datacollector.dto.VideoResponse
import com.drimase.datacollector.network.LogService
import com.drimase.datacollector.util.ProgressRequestBody
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
        val registrationRequest = RegistrationRequest(loginId,password)
        return logService.registration(registrationRequest)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    fun login(loginId:String, password:String): Single<User>{
        return logService.login(loginId, password)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    fun adminLogin(): Single<User>{
        return logService.adminLogin()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }

    fun detect(location: Location) : Single<List<AccidentProneArea>>{
        return logService.detect(location.longitude,location.latitude)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }


    fun logImageLocation(userId:Int,longitude:Double,latitude:Double, file: File): Single<Unit>{
        val requestFile = RequestBody.create(MediaType.parse("multipart/from-data"), file)
        val userId = RequestBody.create(MediaType.parse("multipart/from-data"), userId.toString())
        val longitude = RequestBody.create(MediaType.parse("multipart/from-data"), longitude.toString())
        val latitude = RequestBody.create(MediaType.parse("multipart/from-data"), latitude.toString())
        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)
        return logService.logImageLocation(userId,longitude,latitude,body)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    fun startVideoLog(userId:Int) :Single<VideoResponse>{
        return logService.startVideoLog(userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }

    fun logVideoFrameLocation(userId:Int,videoId:Int,longitude:Double,latitude:Double, file: File): Single<Unit>{
        val image = RequestBody.create(MediaType.parse("multipart/from-data"), file)

        val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("userId",userId.toString())
                .addFormDataPart("videoId",videoId.toString())
                .addFormDataPart("longitude",longitude.toString())
                .addFormDataPart("latitude",latitude.toString())
                .addFormDataPart("image",file.name,image)
                .build()

        return logService.logVideoFrameLocation(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }

    fun stopVideoLog(userId:Int,videoId:Int,longitude:Double,latitude:Double, videoPartFile: ProgressRequestBody): Single<Unit>{

        val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("userId",userId.toString())
                .addFormDataPart("videoId",videoId.toString())
                .addFormDataPart("longitude",longitude.toString())
                .addFormDataPart("latitude",latitude.toString())
                .addFormDataPart("video",videoPartFile.getFilename(),videoPartFile)
                .build()

        return logService.stopVideoLog(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }
}