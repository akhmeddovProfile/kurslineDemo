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
        @Part("UserFullName") userFullName: RequestBody,
        @Part("CompanyName") companyName: RequestBody,
        @Part("CompanyAddress") companyAddress: RequestBody,
        @Part("CompanyAbout") companyAbout: RequestBody,
        @Part("Email") email: RequestBody,
        @Part("MobileNumber") mobileNumber: RequestBody,
        @Part("CompanyStatusId") companyStatusId: RequestBody,
        @Part("CompanyCategoryId") companyCategoryId: RequestBody,
        @Part("CompanyRegionId") companyRegionId: RequestBody,
        @Part("Password") password: RequestBody,
        @Part photos: MultipartBody.Part,
    ):Observable<RegisterCompanyResponse>

}