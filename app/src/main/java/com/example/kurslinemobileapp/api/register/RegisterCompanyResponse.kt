package com.example.kurslinemobileapp.api.register

import com.example.kurslinemobileapp.model.login.UserInfo

data class RegisterCompanyResponse(
    val userInfo: UserInfo,
    val isSuccess: Boolean
)
