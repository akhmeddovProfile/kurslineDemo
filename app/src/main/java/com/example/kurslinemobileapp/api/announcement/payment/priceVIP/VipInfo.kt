package com.example.kurslinemobileapp.api.announcement.payment.priceVIP


import com.google.gson.annotations.SerializedName

data class VipInfo(
    @SerializedName("vipPriceCost")
    val vipPriceCost: String,
    @SerializedName("vipPriceDate")
    val vipPriceDate: Int,
    @SerializedName("vipPriceId")
    val vipPriceId: Int
)