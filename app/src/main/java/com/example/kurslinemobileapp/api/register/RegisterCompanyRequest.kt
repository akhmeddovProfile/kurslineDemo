package com.example.kurslinemobileapp.api.register

import java.io.File

data class RegisterCompanyRequest (
    val userFullName:String,
    val email:String,
    val mobileNumber:String,
    val password:String,
    val gender:Int,
    val companyName:String,
    val companyAddress:String,
    val companyAbout:String,
    val companyCategoryId:String,
    val photos:File
        )