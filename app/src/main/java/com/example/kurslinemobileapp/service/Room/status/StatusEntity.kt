package com.example.kurslinemobileapp.service.Room.status

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "status")
class StatusEntity (
    @PrimaryKey
    val statusId: Int,
    @ColumnInfo(name = "statusName")
    val statusName: String
)