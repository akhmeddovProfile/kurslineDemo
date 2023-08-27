package com.example.kurslinemobileapp.api.paymentPayriff.createOrder

import io.reactivex.Observable
import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface PaymentApiCreateOrder {
    @POST("v2/{method_name}/")
    fun createOrder(@Header("Authorization") secretKey : String, @Path("method_name") methodName : String, @Body params : CreateOrderRequest) : Deferred<CreateOrderResponse>
}
//Secret key: 0B6505100D2941019771F6D3C8DDF6AD