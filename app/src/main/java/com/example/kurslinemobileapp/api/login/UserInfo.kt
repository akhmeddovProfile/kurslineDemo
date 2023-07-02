package com.example.kurslinemobileapp.api.login

data class UserInfo(
    val id: Int,
    val name: String,
    val userType: String,
    val userStatus:Int,
    val isFavorite:Boolean
)