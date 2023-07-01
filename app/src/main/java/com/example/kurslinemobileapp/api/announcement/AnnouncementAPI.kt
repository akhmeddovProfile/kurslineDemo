package com.example.kurslinemobileapp.api.announcement

import com.example.kurslinemobileapp.api.announcement.getDetailAnnouncement.AnnouncementDetailModel
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.GetAllAnnouncement
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AnnouncementAPI {
    @GET("GetAnnouncements")
    fun getAnnouncement() : Observable<GetAllAnnouncement>

    @GET("GetAnnouncementById/{annouuncementId}")
    fun getDataById(@Path("annouuncementId") id: Int): Observable<AnnouncementDetailModel>

    @GET("GetAnnouncements?")
    fun getFilteredItems(
        @Query("limit")limit:Int,
        @Query("offset")offset:Int,
        @Query("RegionId")RegionId:Int,
        @Query("CategoryId")CategoryId:Int,
        @Query("search") search: String,
        @Query("minPrice") minPrice: Double,
        @Query("maxPrice") maxPrice: Double,
        @Query("statusId") statusId: Int,
        @Query("isOnlineId") isOnlineId: Int,
        @Query("userId") userId: Int
    ):Observable<List<GetAllAnnouncement>>
}