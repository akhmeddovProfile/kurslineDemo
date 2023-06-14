package com.example.kurslinemobileapp.api.getInfo

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface InfoAPI {
    @GET("GetUserbyId/{userId}")
    fun getUserInfo(@Header("Authorization") token: String,@Path("userId") userId: Int ): Observable<UserInfoModel>
}