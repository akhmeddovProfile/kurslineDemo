package com.example.kurslinemobileapp.api.announcement

import com.example.kurslinemobileapp.api.announcement.filterAnnouncements.FilterModel
import com.example.kurslinemobileapp.api.announcement.getDetailAnnouncement.AnnouncementDetailModel
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.GetAllAnnouncement
import com.example.kurslinemobileapp.api.announcement.updateanddelete.GetUserAnn
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface AnnouncementAPI {
    @GET("GetAnnouncements")
    fun getAnnouncement() : Observable<GetAllAnnouncement>

    @GET("GetAnnouncementById/{annouuncementId}")
    fun getDataById(@Path("annouuncementId") id: Int): Observable<AnnouncementDetailModel>

    @GET("GetAnnouncementForUserById/{userId}/{announcementId}")
    fun getAnnouncementForUser(@Path("userId") userId: Int,@Path("announcementId") announcementId:Int, @Header("Authorization") token: String)
            : Observable<GetUserAnn>

    @GET("GetAnnouncements")
    fun getFilterProducts(@Query("limit") limit: Int,
                          @Query("offset") offset: Int,
                          @Query("RegionId") regionId: String,
                          @Query("CategoryId") categoryId: String,
                          @Query("search") search: String,
                          @Query("minPrice") minPrice: String,
                          @Query("maxPrice") maxPrice: String,
                          @Query("statusId") statusId: String,
                          @Query("isOnlineId") isOnlineId: String,
                          @Query("userId") userId: Int) : Observable<FilterModel>
}