package com.example.kurslinemobileapp


import com.google.gson.annotations.SerializedName

data class AccessToken(
    @SerializedName("expiresAt")
    val expiresAt: String,
    @SerializedName("token")
    val token: String
)