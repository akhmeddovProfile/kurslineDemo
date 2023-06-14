package com.example.kurslinemobileapp.service

import com.example.kurslinemobileapp.api.register.RegisterAPI
import com.example.kurslinemobileapp.service.Constant.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitService(url: String) {
    private val client = OkHttpService().httpClient.build()
    val retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(client)
        .build()

}