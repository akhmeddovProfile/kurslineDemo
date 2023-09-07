package com.example.kurslinemobileapp.api.announcement.payment.priceMoveForward


import com.google.gson.annotations.SerializedName

data class MoveForwardInfo(
    @SerializedName("irelicekId")
    val irelicekId: Int,
    @SerializedName("irelicekDate")
    val irelicekDate: Int,
    @SerializedName("irelicekCost")
    val irelicekCost: String
    )