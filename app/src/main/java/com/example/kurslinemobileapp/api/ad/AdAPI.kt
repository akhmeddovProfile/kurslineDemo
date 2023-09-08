package com.example.kurslinemobileapp.api.ad

import com.example.kurslinemobileapp.api.getUserCmpDatas.companyAnnouncement.CompanyTransactionAnnouncement
import com.example.kurslinemobileapp.model.AdsModel.AdModel
import io.reactivex.Observable
import retrofit2.http.GET

interface AdAPI {
    @GET("GetReklamlar")
    fun getAds(): Observable<AdModel>

}