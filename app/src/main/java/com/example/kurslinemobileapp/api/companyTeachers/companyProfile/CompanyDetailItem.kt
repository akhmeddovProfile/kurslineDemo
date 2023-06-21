package com.example.kurslinemobileapp.api.companyTeachers.companyProfile

data class CompanyDetailItem(
    val announcements: List<Announcement>,
    val companyAbout: String,
    val companyAddress: String,
    val companyCategory: String,
    val companyImage: String,
    val companyName: String,
    val companyPhone: String
)