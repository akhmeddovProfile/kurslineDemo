package com.example.kurslinemobileapp.api.announcement.getmainAnnouncement

import android.os.Parcel
import android.os.Parcelable
import com.example.kurslinemobileapp.api.companyTeachers.companyProfile.Announcement

data class Announcemenet(
    val announcementDesc: String,
    val announcementName: String,
    val commentCount: Int,
    val companyName: String,
    val id: Int,
    val isOnline: String,
    val isStatus: String,
    val isVIP: Boolean,
    val photos: List<Photo>,
    val price: Int,
    var isFavorite:Boolean,
    val category:String,
    val subCategory:String
)








