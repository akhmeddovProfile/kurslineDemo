package com.example.kurslinemobileapp.modelRegisterLogin


import com.google.gson.annotations.SerializedName

data class UserInfo(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)