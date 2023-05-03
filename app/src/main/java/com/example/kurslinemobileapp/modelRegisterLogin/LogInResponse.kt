package com.example.kurslinemobileapp.modelRegisterLogin


import com.google.gson.annotations.SerializedName

data class LogInResponse(
    @SerializedName("accessToken")
    val accessToken: AccessToken,
    @SerializedName("isEmailVerified")
    val isEmailVerified: Boolean,
    @SerializedName("refreshToken")
    val refreshToken: RefreshToken,
    @SerializedName("userInfo")
    val userInfo: UserInfo
)