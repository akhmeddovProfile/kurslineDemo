package com.example.kurslinemobileapp.api.announcement.payment

import com.example.kurslinemobileapp.api.announcement.payment.priceMoveForward.MoveforwardPriceResponseX
import com.example.kurslinemobileapp.api.announcement.payment.priceVIP.VipPriceResponse
import com.example.kurslinemobileapp.api.announcement.payment.sendOrderInfo.RequestOrderInfo
import com.example.kurslinemobileapp.api.announcement.payment.sendOrderInfo.ResponsePostInfo
import io.reactivex.Observable
import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface Payment {
    @GET("GetVipinfo/{announcementId}/{userId}")
    fun VipPaymentInfo(
        @Path("userId") userId: Int,
        @Path("announcementId") announcementId:Int,
        @Header("Authorization") token: String
    ):Deferred<VipPriceResponse>

    @GET("GetIreliCekInfo/{userId}/{announcementId}")
    fun MoveForwardPaymentInfo(
        @Path("userId") userId: Int,
        @Path("announcementId") announcementId:Int,
        @Header("Authorization") token: String
    ):Deferred<MoveforwardPriceResponseX>

    @GET("GetIreliCekInfo/{userId}/{announcementId}")
    fun MoveForwardPaymentInfo1(
        @Path("userId") userId: Int,
        @Path("announcementId") announcementId:Int,
        @Header("Authorization") token: String
    ):Observable<MoveforwardPriceResponseX>
    @GET("GetIreliCekInfo/{announcementId}/{userId}")
    fun MoveForwardPaymentInfo2(
        @Path("userId") userId: Int,
        @Path("announcementId") announcementId:Int,
        @Header("Authorization") token: String
    ):Observable<MoveforwardPriceResponseX>

    @POST("PostpaymentData/{userId}")
    fun postOrderIformation(
        @Path("userId")userId: Int,
        @Header("Authorization")token: String,
        @Body requestInfo: RequestOrderInfo
    ):Deferred<ResponsePostInfo>

}