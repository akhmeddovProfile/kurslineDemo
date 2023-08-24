package com.example.kurslinemobileapp.api.announcement.payment.priceMoveForward


import com.google.gson.annotations.SerializedName

data class VipInfo(
    @SerializedName("irelicekCost")
    val irelicekCost: String,
    @SerializedName("irelicekDate")
    val irelicekDate: Int,
    @SerializedName("irelicekId")
    val irelicekId: Int
)