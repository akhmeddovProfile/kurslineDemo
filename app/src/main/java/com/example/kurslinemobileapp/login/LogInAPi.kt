package com.example.kurslinemobileapp.login

import com.example.kurslinemobileapp.modelRegisterLogin.LogInResponse
import com.example.kurslinemobileapp.modelRegisterLogin.LoginRequestModel
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface LogInAPi {
    @POST("user-login/")
    fun postLogin(
        @Body params:LoginRequestModel
    ):Observable<LogInResponse>
}