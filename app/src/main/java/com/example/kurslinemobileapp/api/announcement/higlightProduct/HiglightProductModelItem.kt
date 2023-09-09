package com.example.kurslinemobileapp.api.announcement.higlightProduct

data class HiglightProductModelItem(
    val announcementDesc: String,
    val announcementName: String,
    val category: String,
    val commentCount: Int,
    val companyId: Int,
    val companyName: String,
    val id: Int,
    val isFavorite: Boolean,
    val isOnline: String,
    val isStatus: String,
    val isVIP: Boolean,
    val photos: List<Photo>,
    val price: Int,
    val regionName: String,
    val subCategory: String,
    val view: Int
)