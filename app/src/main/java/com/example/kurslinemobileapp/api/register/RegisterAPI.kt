package com.example.kurslinemobileapp.api.register

import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface RegisterAPI {

    @Multipart
    @POST("user-register/")
    fun createUser(  @Part("FullName") fullName: RequestBody,
                    @Part("Email") email: RequestBody,
                    @Part("MobileNumber") mobileNumber: RequestBody,
                    @Part("Password") password:RequestBody,
                    @Part("Gender") gender : RequestBody): Observable<UserRegisterResponse>

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


    @Multipart
    @POST("CreateCompany/{userId}")
    fun userToCompanyCreate(
        @Part("CompanyName") companyName: RequestBody,
        @Part("CompanyCategoryId") companyCategoryId: RequestBody,
        @Part("CompanyAddress") companyAddress: RequestBody,
        @Part("CompanyAbout") companyAbout: RequestBody,
        @Part Photo: MultipartBody.Part,
        @Part("CompanyStatusId") companyStatusId: RequestBody,
        @Header("Authorization") token: String, @Path("userId") userId: Int
    ):Observable<UserToCompanyResponse>
}