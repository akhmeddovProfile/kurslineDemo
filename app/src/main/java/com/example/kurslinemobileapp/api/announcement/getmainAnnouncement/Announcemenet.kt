package com.example.kurslinemobileapp.api.announcement.getmainAnnouncement

data class Announcemenet(
    val announcemementDesc: String,
    val announcemementName: String,
    val commentCount: Int,
    val companyName: String,
    val id: Int,
    val isOnline: String,
    val isRejim: String,
    val isVIP: Boolean,
    val photos: List<Photo>,
    val price: Int
)