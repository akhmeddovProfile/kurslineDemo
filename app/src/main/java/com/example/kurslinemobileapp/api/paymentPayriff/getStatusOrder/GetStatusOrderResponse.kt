package com.example.kurslinemobileapp.api.paymentPayriff.getStatusOrder

import com.google.gson.annotations.SerializedName

data class GetStatusOrderResponse(
    @SerializedName("code")
    var code : String,
    @SerializedName("internalMessage")
    var internalMessage : String,
    @SerializedName("message")
    var message : String,
    @SerializedName("payload")
    var payload : GetStatusOrderResponsePayload) {
}

data class GetStatusOrderResponsePayload(
    @SerializedName("orderId")
    var orderId : String,
    @SerializedName("orderStatus")
    var orderStatus : String) {
}