package com.example.kurslinemobileapp.api.announcement.payment.sendOrderInfo

import com.google.gson.annotations.SerializedName


data class RequestOrderInfoVip(
    @SerializedName("announcementId")
    val announcementId: Int,
    @SerializedName("ireliCekId")
    val ireliCekId: Any?,
    @SerializedName("orderKey")
    val orderKey: String,
    @SerializedName("sessionId")
    val sessionId: String,
    @SerializedName("vipId")
    val vipId: Any?
)