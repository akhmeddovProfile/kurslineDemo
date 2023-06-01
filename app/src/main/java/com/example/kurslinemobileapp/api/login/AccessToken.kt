package com.example.kurslinemobileapp.api.login

data class AccessToken(
    val expiresAt: String,
    val token: String
)