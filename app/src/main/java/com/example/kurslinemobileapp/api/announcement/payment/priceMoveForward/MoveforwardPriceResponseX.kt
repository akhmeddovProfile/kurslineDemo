package com.example.kurslinemobileapp.api.announcement.payment.priceMoveForward


import com.google.gson.annotations.SerializedName

data class MoveforwardPriceResponseX(
    @SerializedName("elanInfo")
    val elanInfo: String,
    @SerializedName("vipInfo")
    val ireliCekInfo: List<MoveForwardInfo>
)