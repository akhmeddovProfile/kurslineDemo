package com.example.kurslinemobileapp.api.paymentPayriff.getStatusOrder

import com.google.gson.annotations.SerializedName

data class GetStatusOrderRequest(
    @SerializedName("body")
    var body : GetStatusOrderRequestBody,
    @SerializedName("merchant")
    var merchant : String) {
}

data class GetStatusOrderRequestBody(
    @SerializedName("language")
    var language : String,
    @SerializedName("orderId")
    var orderId : String,
    @SerializedName("sessionId")
    var sessionId : String) {
}
