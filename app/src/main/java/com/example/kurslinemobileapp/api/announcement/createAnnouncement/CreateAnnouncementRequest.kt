package com.example.kurslinemobileapp.api.announcement.createAnnouncement

import java.math.BigDecimal

data class CreateAnnouncementRequest(
    val AnnouncementName:String,
    val AnnouncementDesc:String,
    val AnnouncementPrice:BigDecimal,
    val AnnouncementSubCategoryId:Int,
    val AnnouncementRegionId:Int,
    val AnnouncementIsOnlineId:Int,
    
)
