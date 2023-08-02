package com.example.kurslinemobileapp.api.login

import io.reactivex.Observable
import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface LogInAPi {
    @POST("user-login/")
    fun postLogin(
        @Body params: LoginRequest
    ): Observable<LoginResponseX>


    @Multipart
    @POST("send-password-reset-email")
    fun resetPassword(
        @Part("email")email:RequestBody
    ): Deferred<ResetPasswordResponse>
}