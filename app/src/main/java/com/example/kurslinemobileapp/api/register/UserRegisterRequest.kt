package com.example.kurslinemobileapp.api.register

data class UserRegisterRequest(
    var fullName: String,
    var email: String,
    var mobileNumber: String,
    var password: String,
    var gender: String
)