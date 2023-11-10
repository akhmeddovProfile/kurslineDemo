package com.example.kurslinemobileapp.api.login


import com.google.gson.annotations.SerializedName

data class LogInResponseOTP(
    @SerializedName("accessToken")
    val accessToken: AccessTokenX,
    @SerializedName("refreshToken")
    val refreshToken: RefreshTokenX,
    @SerializedName("userInfo")
    val userInfo: UserInfoX
)