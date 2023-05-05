package com.example.kurslinemobileapp.api.login

import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface LogInAPi {
    @POST("user-login/")
    fun postLogin(
        @Body params: LoginRequest
    ): Observable<LogInResponse>
}