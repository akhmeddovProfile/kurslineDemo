package com.example.kurslinemobileapp.model.login


import com.google.gson.annotations.SerializedName

data class AccessToken(
    @SerializedName("expiresAt")
    val expiresAt: String,
    @SerializedName("token")
    val token: String
)