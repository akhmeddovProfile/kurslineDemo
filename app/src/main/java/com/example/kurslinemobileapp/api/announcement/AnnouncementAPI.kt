package com.example.kurslinemobileapp.api.announcement

import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.GetAllAnnouncement
import io.reactivex.Observable
import retrofit2.http.GET

interface AnnouncementAPI {
    @GET("GetAnnouncements")
    fun getAnnouncement() : Observable<GetAllAnnouncement>
}