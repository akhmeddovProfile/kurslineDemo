package com.example.kurslinemobileapp.api.updateUserCompany

import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
interface UpdateAPI {

    @Multipart
    @POST("Update-user/{userId}")
    fun userUpdateMethod(
        @Part("FullName") fullName: RequestBody,
        @Part("Email") email: RequestBody,
        @Part("MobileNumber") mobileNumber: RequestBody,
        @Part("Gender") gender: RequestBody,
        @Part photos: MultipartBody.Part?,
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Observable<UpdateResponse>


    @Multipart
    @POST("Update-Company/{userId}")
    fun companyUpdateMethod(
        @Part("UserFullName") userFullName: RequestBody,
        @Part("Email") email: RequestBody,
        @Part("MobileNumber") mobileNumber: RequestBody,
        @Part("Gender") gender: RequestBody,
        @Part("CompanyName") companyName: RequestBody,
        @Part("CompanyAddress") companyAddress: RequestBody,
        @Part("CompanyAbout") companyAbout: RequestBody,
        @Part photos: MultipartBody.Part?,
        @Part("CompanyStatusId") companyStatusId: RequestBody,
        @Part("CompanyCategoryId") companyCategoryId: RequestBody,
        @Part("CompanyRegionId") companyRegionId: RequestBody,
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Observable<UpdateResponse>
}
