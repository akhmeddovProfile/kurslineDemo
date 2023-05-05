package com.example.kurslinemobileapp.api.register

import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface RegisterAPI {

    @POST("user-register/")
    fun createAPI(@Body registerRequest: RegisterRequest): Observable<RegisterResponse>
}