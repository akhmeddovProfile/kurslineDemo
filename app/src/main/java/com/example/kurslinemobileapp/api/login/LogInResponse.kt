package com.example.kurslinemobileapp.api.login

import com.example.kurslinemobileapp.model.login.AccessToken
import com.example.kurslinemobileapp.model.login.RefreshToken
import com.example.kurslinemobileapp.model.login.UserInfo
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