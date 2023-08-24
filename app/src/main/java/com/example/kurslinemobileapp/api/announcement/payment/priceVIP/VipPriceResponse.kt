package com.example.kurslinemobileapp.api.announcement.payment.priceVIP


import com.google.gson.annotations.SerializedName

data class VipPriceResponse(
    @SerializedName("elanInfo")
    val elanInfo: String,
    @SerializedName("vipInfo")
    val vipInfo: List<VipInfo>
)