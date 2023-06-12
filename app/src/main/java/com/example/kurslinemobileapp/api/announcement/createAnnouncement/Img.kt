package com.example.kurslinemobileapp.api.announcement.createAnnouncement


import com.google.gson.annotations.SerializedName

data class Img(
    @SerializedName("name")
    val name: String,
    @SerializedName("photo")
    val photo: String
)