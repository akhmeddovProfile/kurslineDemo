package com.example.kurslinemobileapp.model.login

import com.google.gson.annotations.SerializedName

data class UserInfo(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)