package com.example.kurslinemobileapp.register

import com.example.kurslinemobileapp.modelRegisterLogin.RegisterModel
import com.example.kurslinemobileapp.modelRegisterLogin.RegisterResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface RegisterAPI {

    @POST("user-register/")
    fun createAPI(@Body registerModel: RegisterModel): Observable<RegisterResponse>
}