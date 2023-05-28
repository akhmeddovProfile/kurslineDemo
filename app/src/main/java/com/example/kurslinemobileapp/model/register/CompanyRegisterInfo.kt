package com.example.kurslinemobileapp.model.register

import com.google.gson.annotations.SerializedName

data class CompanyRegisterInfo(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("mobileNumber")
    val mobileNumber: String,
    @SerializedName("photo")
    val photo: String

)
