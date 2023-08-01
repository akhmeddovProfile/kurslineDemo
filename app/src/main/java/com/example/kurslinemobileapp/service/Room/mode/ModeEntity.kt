package com.example.kurslinemobileapp.service.Room.mode

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "mode")
class ModeEntity (
    @PrimaryKey
    val modeId: Int,
    @ColumnInfo(name = "modeName")
    val modeName: String
)