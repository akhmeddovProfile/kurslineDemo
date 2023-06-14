package com.example.kurslinemobileapp.api.getInfo

data class UserInfoModel(
    val companyAbout: Any,
    val companyAddress: Any,
    val companyCategoryId: Any,
    val companyName: Any,
    val companyPhoto: String,
    val email: String,
    val fullName: String,
    val gender: Int,
    val mobileNumber: String,
    val photo: Any,
    val userStatusId: Int
)