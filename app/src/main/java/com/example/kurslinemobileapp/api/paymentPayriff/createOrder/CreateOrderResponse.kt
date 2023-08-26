package com.example.kurslinemobileapp.api.paymentPayriff.createOrder

import com.google.gson.annotations.SerializedName

data class CreateOrderResponse(
    @SerializedName("code")
    var code : String,
    @SerializedName("internalMessage")
    var internalMessage : String,
    @SerializedName("message")
    var message : String,
    @SerializedName("payload")
    var payload : CreateOrderResponsePayload) {
}

data class CreateOrderResponsePayload(
    @SerializedName("orderId")
    var orderId : String,
    @SerializedName("paymentUrl")
    var paymentUrl : String,
    @SerializedName("sessionId")
    var sessionId : String) {
}