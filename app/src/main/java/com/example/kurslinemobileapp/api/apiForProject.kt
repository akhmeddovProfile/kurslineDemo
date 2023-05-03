package com.example.kurslinemobileapp.api

import com.example.kurslinemobileapp.modelRegisterLogin.RegisterModel
import com.example.kurslinemobileapp.modelRegisterLogin.RegisterResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface apiForProject {

    @POST("user-register/")
    fun createAPI(@Body registerModel: RegisterModel): Observable<RegisterResponse>
}