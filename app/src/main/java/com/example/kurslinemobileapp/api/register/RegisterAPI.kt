package com.example.kurslinemobileapp.api.register

import io.reactivex.Observable
import okhttp3.MultipartBody
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
        @Part ("userFullName") userFullName:RegisterCompanyRequest,
        @Part ("email") email:RegisterCompanyRequest,
        @Part ("mobileNumber") mobileNumber:RegisterCompanyRequest,
        @Part ("password") password:RegisterCompanyRequest,
        @Part ("gender") gender:RegisterCompanyRequest,
        @Part ("companyName") companyName:RegisterCompanyRequest,
        @Part ("companyAddress") companyAddress:RegisterCompanyRequest,
        @Part ("companyAbout") companyAbout:RegisterCompanyRequest,
        @Part ("companyCategoryId") companyCategoryId:RegisterCompanyRequest,
        @Part ("photos") photos:MultipartBody.Part
    ):retrofit2.Call<RegisterCompanyResponse>

}