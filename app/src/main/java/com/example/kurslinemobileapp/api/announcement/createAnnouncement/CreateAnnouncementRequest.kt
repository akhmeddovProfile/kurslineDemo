package com.example.kurslinemobileapp.api.announcement.createAnnouncement


import com.google.gson.annotations.SerializedName

data class CreateAnnouncementRequest(
    val AnnouncementName: String,
    val AnnouncementDesc: String,
    val AnnouncementPrice: Int,
    val AnnouncementAddress: String,
    val AnnouncementSubCategoryId: String,
    val AnnouncementRegionId: String,
    val Teacher: List<String>,
    val AnnouncementIsOnlineId: String,
    val img: List<Img>
)