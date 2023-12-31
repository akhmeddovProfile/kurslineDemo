package com.example.kurslinemobileapp.api.announcement.getDetailAnnouncement

import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.Announcemenet

data class AnnouncementDetailModel(
    val announcementAddress: String,
    val announcementDesc: String,
    val announcementEndDate: String,
    val announcementStartDate: String,
    val announcementIsActive: Boolean,
    val announcementName: String,
    val announcementPrice: Int,
    val announcementRegionId: String,
    val announcementCategoryName : String,
    val announcementSubCategoryName: String,
    val comments: List<Comment>,
    val companyName: String,
    val countView: Int,
    val id: String,
    val isOnline: String,
    val isVIP: Boolean,
    val phone: String,
    val photos: List<Photo>,
    val teacher: List<String>,
    val announcements:List<AnnouncementSimilarCourse>
)