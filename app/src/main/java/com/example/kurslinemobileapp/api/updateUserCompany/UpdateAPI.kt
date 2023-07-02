package com.example.kurslinemobileapp.api.updateUserCompany

import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface UpdateAPI {

    @Multipart
    @POST("Update-user/{userId}")
    fun userUpdateMethod(
        @Part("FullName") fullName:RequestBody,
        @Part("Email") email:RequestBody,
        @Part("MobileNumber") mobileNumber:RequestBody,
        @Part("Gender") gender:RequestBody,
        @Part Photos: MultipartBody.Part,
        @Header("Authorization") token: String, @Path("userId") userId: Int
    ): Observable<UpdateResponse>
}