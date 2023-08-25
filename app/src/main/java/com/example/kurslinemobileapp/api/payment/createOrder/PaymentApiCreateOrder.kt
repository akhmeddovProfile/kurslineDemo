package com.example.kurslinemobileapp.api.payment.createOrder

import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface PaymentApiCreateOrder {
    @POST("v2/{method_name}/")
    fun createOrder(@Header("Authorization") secretKey : String, @Path("method_name") methodName : String, @Body params : CreateOrderRequest) : Observable<CreateOrderResponse>
}
