package com.example.kurslinemobileapp.api.announcement

import com.example.kurslinemobileapp.api.announcement.getDetailAnnouncement.AnnouncementDetailModel
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.GetAllAnnouncement
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface AnnouncementAPI {
    @GET("GetAnnouncements")
    fun getAnnouncement() : Observable<GetAllAnnouncement>

    @GET("GetAnnouncementById/{annouuncementId}")
    fun getDataById(@Path("annouuncementId") id: Int): Observable<AnnouncementDetailModel>
}