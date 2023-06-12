package com.example.kurslinemobileapp.api.getInfo

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface InfoAPI {
    @GET("GetUserbyId/{userId}")
    fun getUserInfo( @Path("userId") userId: Int,
        @Header("Authorization") token: String): Observable<UserInfoModel>
}