package com.example.kurslinemobileapp.api.announcement.getmainAnnouncement

data class GetAllAnnouncement(
    val announcemenets: List<Announcemenet>,
    val total: Int
)