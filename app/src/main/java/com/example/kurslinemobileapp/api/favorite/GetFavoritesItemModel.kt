package com.example.kurslinemobileapp.api.favorite


data class GetFavoritesItemModel(
    val announcementDesc: String,
    val announcementName: String,
    val commentCount: Int,
    val companyName: String,
    val id: Int,
    val isOnline: String,
    val isStatus: Any,
    val isVIP: Boolean,
    val photos: List<Photo>,
    val price: Int
)