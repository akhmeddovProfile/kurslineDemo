package com.example.kurslinemobileapp.api.announcement.updateanddelete

import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.Announcemenet

data class GetUserAnn(
    val announcementAddress: String,
    val announcementDesc: String,
    val announcementEndDate: String,
    val announcementIsActive: Boolean,
    val announcementName: String,
    val announcementPrice: Int,
    val announcementRegionId: Int,
    val announcementSubCategoryId: Int,
    val categoryId: Int,
    val companyName: String,
    val countView: Int,
    val id: Int,
    val isOnline: String,
    val isVIP: Boolean,
    val phone: String,
    val photos: List<Photo>,
    val teacher: List<String>

)