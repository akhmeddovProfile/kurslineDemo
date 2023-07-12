package com.example.kurslinemobileapp.api.announcement.createAnnouncement


import com.google.gson.annotations.SerializedName

data class CreateAnnouncementRequest(
    val announcementName: String,
    val announcementDesc: String,
    val announcementPrice: Int,
    val announcementAddress: String,
    val announcementIsOnlineId: Int,
    val announcementCategoryId:Int,
    val announcementSubCategoryId: Int,
    val announcementRegionId: Int,
    val img: List<Img>,
    val teacher: List<String>
)