package com.drimase.datacollector.network

import com.drimase.datacollector.dto.AccidentProneArea
import com.drimase.datacollector.dto.Location
import com.drimase.datacollector.dto.RegistrationRequest
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
    @POST("/log")
    fun logImageLocation(
            @Part("userId") userId: RequestBody,
            @Part("longitude") longitude: RequestBody,
            @Part("latitude") latitude: RequestBody,
            @Part image: MultipartBody.Part
    ): Single<Unit>

}