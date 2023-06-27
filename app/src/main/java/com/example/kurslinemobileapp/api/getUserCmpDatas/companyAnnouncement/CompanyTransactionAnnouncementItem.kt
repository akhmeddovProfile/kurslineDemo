package com.example.kurslinemobileapp.api.getUserCmpDatas.companyAnnouncement

data class CompanyTransactionAnnouncementItem(
    val announcementDesc: String,
    val announcementName: String,
    val commentCount: Int,
    val companyName: String,
    val id: Int,
    val isOnline: String,
    val isStatus: String,
    val isVIP: Boolean,
    val photos: List<Photo>,
    val price: Int
)