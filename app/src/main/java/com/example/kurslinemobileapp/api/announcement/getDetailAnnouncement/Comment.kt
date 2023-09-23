package com.example.kurslinemobileapp.api.announcement.getDetailAnnouncement

data class Comment(
    val commentContent: String,
    var commentId: Int,
    val userFullName: String
)