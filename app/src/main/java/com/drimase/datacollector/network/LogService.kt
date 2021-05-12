package com.drimase.datacollector.network

import com.drimase.datacollector.dto.*
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
        @Body registrationRequest: RegistrationRequest
    ): Single<User>


    @GET("/user/admin")
    fun adminLogin(
    ): Single<User>


    @GET("/detect/{longitude}/{latitude}")
    fun detect(
            @Path("longitude") longitude: Double,
            @Path("latitude") latitude: Double
    ): Single<List<AccidentProneArea>>

    @Multipart
    @POST("/log/image")
    fun logImageLocation(
            @Part("userId") userId: RequestBody,
            @Part("longitude") longitude: RequestBody,
            @Part("latitude") latitude: RequestBody,
            @Part image: MultipartBody.Part
    ): Single<Unit>


    @GET("/log/video/start/{userId}")
    fun startVideoLog(
            @Path("userId") userId: Int,
    ): Single<VideoResponse>


    @Multipart
    @POST("/log/video/frame")
    fun logVideoFrameLocation(
            @Part("userId") userId: RequestBody,
            @Part("videoId") videoId: RequestBody,
            @Part("longitude") longitude: RequestBody,
            @Part("latitude") latitude: RequestBody,
            @Part image: MultipartBody.Part
    ): Single<Unit>

    @POST("/log/video/frame")
    fun logVideoFrameLocation(
            @Body requestBody: MultipartBody
    ): Single<Unit>

    /*
    @Multipart
    @POST("/log/video/stop")
    fun stopVideoLog(
            @Part("userId") userId: RequestBody,
            @Part("videoId") videoId: RequestBody,
            @Part("longitude") longitude: RequestBody,
            @Part("latitude") latitude: RequestBody,
            @Part video: MultipartBody.Part
    ): Single<Unit>
    */

    @POST("/log/video/stop")
    fun stopVideoLog(
            @Body requestBody: MultipartBody
    ): Single<Unit>

}