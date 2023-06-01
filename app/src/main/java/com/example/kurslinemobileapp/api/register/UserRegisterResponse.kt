package com.example.kurslinemobileapp.api.register

data class UserRegisterResponse(
    val isSuccess: Boolean,
    val code :String,
    val description:String,
    val details:String
)