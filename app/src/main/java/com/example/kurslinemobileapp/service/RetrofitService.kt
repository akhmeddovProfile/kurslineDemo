package com.example.kurslinemobileapp.service

import com.example.kurslinemobileapp.api.announcement.AnnouncementAPI
import com.example.kurslinemobileapp.api.announcement.payment.Payment
import com.example.kurslinemobileapp.api.login.LogInAPi
import com.example.kurslinemobileapp.api.register.RegisterAPI
import com.example.kurslinemobileapp.service.Constant.BASE_URL
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class RetrofitService(url: String) {
    private val client = OkHttpService().httpClient.build()
    val retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(client)
        .build()

    val retrofit2: Retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory()) // Import this from retrofit2-kotlin-coroutines-adapter library
        .client(client)
        .build()

    val apiService: LogInAPi = retrofit2.create(LogInAPi::class.java)
    val apiServicewriteUs :AnnouncementAPI=retrofit2.create(AnnouncementAPI::class.java)
    val apiServicemoveForwardInfo :Payment=retrofit2.create(Payment::class.java)


}