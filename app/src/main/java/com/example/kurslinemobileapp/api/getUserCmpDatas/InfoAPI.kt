package com.example.kurslinemobileapp.api.getUserCmpDatas

import com.example.kurslinemobileapp.api.getUserCmpDatas.UserCmpInfoModel.UserInfoModel
import com.example.kurslinemobileapp.api.getUserCmpDatas.companyAnnouncement.CompanyTransactionAnnouncement
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface InfoAPI {
    @GET("GetUserbyId/{userId}")
    fun getUserInfo(@Header("Authorization") token: String,@Path("userId") userId: Int ): Observable<UserInfoModel>

    @GET("User/GetAnnouncements/{userId}?limit=10&offset=0&IsActive=1")
    fun getActiveAnnouncements(@Header("Authorization") token: String,@Path("userId") userId: Int ): Observable<CompanyTransactionAnnouncement>

    @GET("User/GetAnnouncements/{userId}?limit=10&offset=0&IsActive=3")
    fun getWaitAnnouncements(@Header("Authorization") token: String,@Path("userId") userId: Int ): Observable<CompanyTransactionAnnouncement>

    @GET("User/GetAnnouncements/{userId}?limit=10&offset=0&IsActive=2")
    fun getDeactiveAnnouncements(@Header("Authorization") token: String,@Path("userId") userId: Int ): Observable<CompanyTransactionAnnouncement>
}