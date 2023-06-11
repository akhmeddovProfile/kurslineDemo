package com.example.kurslinemobileapp.api.getInfo

import com.example.kurslinemobileapp.api.companyTeachers.CompanyTeacherModel
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface InfoAPI {
    @GET("GetUserbyId/{userId}")
    fun getUserInfo( @Path("userId") userId: Int,
        @Header("Authorization") token: String): Observable<UserInfoModel>
}