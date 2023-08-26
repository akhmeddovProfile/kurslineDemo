package com.example.kurslinemobileapp.api.paymentPayriff.getStatusOrder

import io.reactivex.Observable
import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface PaymentApiGetOrderStatus {
    @POST("{method_name}/")
    fun getStatusOrder(@Header("Authorization") secretKey : String, @Path("method_name") methodName : String, @Body params : GetStatusOrderRequest) : Deferred<GetStatusOrderResponse>
}