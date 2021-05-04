package com.drimase.datacollector.network

import com.drimase.datacollector.dto.Location
import com.drimase.datacollector.dto.User
import io.reactivex.rxjava3.core.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface LogService {
    @GET("/user/login/{loginId}/{password}")
    fun login(
        @Path("loginId") loginId: String,
        @Path("password") password: String
    ): Single<User>

    @POST("/user")
    fun registration(
        @Body loginId:String,
        @Body password: String
    ): Single<User>


    @POST("/log/start/{userId}")
    fun logStart(
        @Path("userId") userId: Int,
    ): Single<Void>


    /*
    //아래 미완 -> 비디오 아이디 받아야 위치 저장 가능
    @POST("/log/location/{userId}")
    fun logLocation(
        @Path("userId") userId: Int,
        @Body location: Location
    ): Single<Void>
    */

    //아래 미완 -> 비디오 아이디 받아야 위치 저장 가능
    @Multipart
    @POST("/log/{userId}")
    fun logImageLocation(
            @Path("userId") userId: Int,
            @Part("longitude") longitude: RequestBody,
            @Part("latitude") latitude: RequestBody,
            @Part image: MultipartBody.Part
    ): Single<Void>


    /*
    //아래 미완 -> 비디오 아이디 받아야 위치 저장 가능
    @Multipart
    @POST("/log/stop/{userId}")
    fun uploadVideo(
        @Path("userId") userId: Int,
        @Part("videoId") videoId: RequestBody,
        @Part video: MultipartBody.Part
    ): Single<Void>
    */
}