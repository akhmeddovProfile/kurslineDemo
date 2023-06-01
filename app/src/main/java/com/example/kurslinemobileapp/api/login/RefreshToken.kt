package com.example.kurslinemobileapp.api.login

data class RefreshToken(
    val expiresAt: String,
    val token: String
)