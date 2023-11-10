package com.example.kurslinemobileapp.api.login

data class LoginRequest(
    var email: String,
    var password: String
)

data class LoginOTPRequest(
    var number:String,
    var code:String
)