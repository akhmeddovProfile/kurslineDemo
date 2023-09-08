package com.example.kurslinemobileapp.api.announcement.updateanddelete


import com.google.gson.annotations.SerializedName

data class GetAnnouncementResponse(
    @SerializedName("announcementAddress")
    val announcementAddress: String,
    @SerializedName("announcementCategoryId")
    val announcementCategoryId: Int,
    @SerializedName("announcementDesc")
    val announcementDesc: String,
    @SerializedName("announcementEndDate")
    val announcementEndDate: String,
    @SerializedName("announcementIsActive")
    val announcementIsActive: Boolean,
    @SerializedName("announcementName")
    val announcementName: String,
    @SerializedName("announcementPrice")
    val announcementPrice: Int,
    @SerializedName("announcementRegionId")
    val announcementRegionId: Int,
    @SerializedName("announcementSubCategoryId")
    val announcementSubCategoryId: Int,
    @SerializedName("companyName")
    val companyName: String,
    @SerializedName("countView")
    val countView: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("isOnline")
    val isOnline: Int,
    @SerializedName("isVIP")
    val isVIP: Boolean,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("photos")
    val photos: List<PhotoX>,
    @SerializedName("teacher")
    val teacher: List<String>
)