package com.example.kurslinemobileapp.modelRegisterLogin


import com.google.gson.annotations.SerializedName

data class AccessToken(
    @SerializedName("expiresAt")
    val expiresAt: String,
    @SerializedName("token")
    val token: String
)