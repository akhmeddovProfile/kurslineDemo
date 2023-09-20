package com.example.kurslinemobileapp.api.announcement.createAnnouncement


data class CreateAnnouncementRequest(
    val announcementName: String,
    val announcementDesc: String,
    val announcementPrice: String,
    val announcementAddress: String,
    val announcementIsOnlineId: Int,
    val announcementCategoryId:Int,
    val announcementSubCategoryId: Int,
    val announcementRegionId: Int,
    val img: List<Img>,
    val teacher: List<String>
)