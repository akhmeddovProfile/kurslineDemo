package com.example.kurslinemobileapp.api.payment.createOrder

import com.google.gson.annotations.SerializedName

data class CreateOrderRequest(
    @SerializedName("body")
    var body : CreateOrderRequestBody,
    @SerializedName("merchant")
    var merchant : String) {
}

data class CreateOrderRequestBody(
    @SerializedName("amount")
    var amount : Double,
    @SerializedName("approveURL")
    var approveURL : String,
    @SerializedName("cancelURL")
    var cancelURL : String,
    @SerializedName("cardUuid")
    var cardUuid : String,
    @SerializedName("currencyType")
    var currencyType : String,
    @SerializedName("declineURL")
    var declineURL : String,
    @SerializedName("description")
    var description : String,
    @SerializedName("directPay")
    var directPay : Boolean,
    @SerializedName("installmentPeriod")
    var installmentPeriod : Int,
    @SerializedName("installmentProductType")
    var installmentProductType : String,
    @SerializedName("language")
    var language : String,
    @SerializedName("senderCardUID")
    var senderCardUID : String) {
}


