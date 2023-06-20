package com.example.kurslinemobileapp.api.announcement.createAnnouncement


import com.google.gson.annotations.SerializedName

data class CreateAnnouncementRequest(
    val announcementAddress: String,
    val announcementDesc: String,
    val announcementIsOnlineId: Int,
    val announcementName: String,
    val announcementPrice: Int,
    val announcementRegionId: Int,
    val announcementSubCategoryId: Int,
    val img: List<Img>,
    val teacher: List<String>
)