package com.example.kurslinemobileapp.api.announcement.createAnnouncement


import com.google.gson.annotations.SerializedName

data class CreateAnnouncementRequest(
    @SerializedName("announcementAddress")
    val announcementAddress: String,
    @SerializedName("announcementDesc")
    val announcementDesc: String,
    @SerializedName("announcementIsOnlineId")
    val announcementIsOnlineId: Int,
    @SerializedName("announcementName")
    val announcementName: String,
    @SerializedName("announcementPrice")
    val announcementPrice: Int,
    @SerializedName("announcementRegionId")
    val announcementRegionId: Int,
    @SerializedName("announcementSubCategoryId")
    val announcementSubCategoryId: Int,
    @SerializedName("img")
    val img: List<Img>,
    @SerializedName("teacher")
    val teacher: List<String>
)