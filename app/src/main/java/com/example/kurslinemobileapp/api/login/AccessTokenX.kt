package com.example.kurslinemobileapp.api.login


import com.google.gson.annotations.SerializedName

data class AccessTokenX(
    @SerializedName("expiresAt")
    val expiresAt: String,
    @SerializedName("token")
    val token: String
)