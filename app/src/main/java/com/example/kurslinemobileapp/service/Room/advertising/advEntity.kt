package com.example.kurslinemobileapp.service.Room.advertising

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "adv")
class advEntity (
    @PrimaryKey
    val reklamId: Int,
    val reklamText: String,
    val reklamPhoto: String,
    val reklamLink: String

)