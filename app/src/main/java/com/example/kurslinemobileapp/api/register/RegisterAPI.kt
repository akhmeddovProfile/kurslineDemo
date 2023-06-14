package com.example.kurslinemobileapp.api.register

import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface RegisterAPI {

    @POST("user-register/")
    fun createAPI(@Body registerRequest: UserRegisterRequest): Observable<UserRegisterResponse>

    @Multipart
    @POST("company-register/")
    fun createCompany(
        @Part("userFullName") userFullName: RequestBody,
        @Part("email") email: RequestBody,
        @Part("mobileNumber") mobileNumber: RequestBody,
        @Part("password") password: RequestBody,
        @Part("companyName") companyName: RequestBody,
        @Part("companyAddress") companyAddress: RequestBody,
        @Part("companyAbout") companyAbout: RequestBody,
        @Part("companyCategoryId") companyCategoryId: RequestBody,
        @Part photos: MultipartBody.Part,
        @Part("companyStatusId") companyStatusId: RequestBody,
        @Part("companyRegionId") companyRegionId: RequestBody
    ):Single<RegisterCompanyResponse>

}