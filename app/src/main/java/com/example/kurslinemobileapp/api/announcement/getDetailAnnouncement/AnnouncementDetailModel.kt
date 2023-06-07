package com.example.kurslinemobileapp.api.announcement.getDetailAnnouncement

data class AnnouncementDetailModel(
    val announcementAddress: String,
    val announcementDesc: String,
    val announcementEndDate: String,
    val announcementIsActive: Boolean,
    val announcementName: String,
    val announcementPrice: Int,
    val announcementRegionId: String,
    val announcementSubCategoryId: String,
    val comments: List<Any>,
    val companyName: String,
    val countView: Any,
    val id: Int,
    val isOnline: String,
    val isVIP: Boolean,
    val phone: String,
    val photos: List<Any>,
    val teacher: List<String>
)