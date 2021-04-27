package com.drimase.datacollector.network

import com.drimase.datacollector.dto.Location
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface LogService {
    @POST("/log")
    fun logLocation(
        @Body location: Location
    )

    @Multipart
    @POST("/video")
    fun uploadVideo(
        @Part("student_id") studentId: RequestBody,
        @Part video: MultipartBody.Part
    ): Call<Void>
}