package com.example.kurslinemobileapp.api.announcement.filterAnnouncements

data class Announcemenet(
    val announcementDesc: String,
    val announcementName: String,
    val commentCount: Int,
    val companyName: String,
    val id: Int,
    val isFavorite: Boolean,
    val isOnline: String,
    val isStatus: String,
    val isVIP: Boolean,
    val photos: List<Photo>,
    val price: Int
)