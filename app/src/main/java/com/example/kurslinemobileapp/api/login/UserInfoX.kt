package com.example.kurslinemobileapp.api.login


import com.google.gson.annotations.SerializedName

data class UserInfoX(
    @SerializedName("id")
    val id: Int,
    @SerializedName("isFavorite")
    val isFavorite: Boolean,
    @SerializedName("name")
    val name: String,
    @SerializedName("number")
    val number: String,
    @SerializedName("photo")
    val photo: String,
    @SerializedName("userStatus")
    val userStatus: Int,
    @SerializedName("userType")
    val userType: String
)