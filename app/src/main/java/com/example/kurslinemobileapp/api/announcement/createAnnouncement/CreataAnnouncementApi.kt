package com.example.kurslinemobileapp.api.announcement.createAnnouncement

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface CreataAnnouncementApi {

    @Multipart
    @POST("CreateAnnouncement/{id}")
    fun createAnnouncement(@Path("id") id:String,
                           @Part ("AnnouncementName") AnnouncementName:RequestBody,
                           @Part("AnnouncementDesc")AnnouncementDesc:RequestBody,
                           @Part("AnnouncementAddress")AnnouncementAddress:RequestBody,
                           @Part("AnnouncementPrice")AnnouncementPrice:RequestBody,
                           @Part("AnnouncementSubCategoryId")AnnouncementSubCategoryId:RequestBody,
                           @Part ("AnnouncementRegionId")AnnouncementRegionId:RequestBody,
                           @Part("AnnouncementIsOnlineId")AnnouncementIsOnlineId:RequestBody,
                           @Part("Teacher")Teacher:RequestBody,
                           @Part Photos:MultipartBody.Part
                           )
}