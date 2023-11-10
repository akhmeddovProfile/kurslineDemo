package com.example.kurslinemobileapp.api.login

data class LoginResponseX(
    val accessToken: AccessToken,
    val isEmailVerified: Boolean,
    val refreshToken: RefreshToken,
    val userInfo: UserInfo
)

