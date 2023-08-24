package com.example.kurslinemobileapp.api.announcement.payment

import com.example.kurslinemobileapp.api.announcement.payment.priceMoveForward.MoveforwardPriceResponseX
import com.example.kurslinemobileapp.api.announcement.payment.priceVIP.VipPriceResponse
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface Payment {
    @GET("GetVipinfo/{annouuncementId}/{userId}")
    fun VipPaymentInfo(
        @Header("Authorization") token: String,
        @Path("announcementId") announcementId:Int,
        @Path("userId") userId: Int
    ):Deferred<VipPriceResponse>

    @GET("GetIreliCekInfo/{userId}/{announcementId}")
    fun MoveForwardPaymentInfo(
        @Path("userId") userId: Int,
        @Path("announcementId") announcementId:Int,
        @Header("Authorization") token: String
    ):Deferred<MoveforwardPriceResponseX>

}