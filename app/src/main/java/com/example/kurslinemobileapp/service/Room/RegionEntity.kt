package com.example.kurslinemobileapp.service.Room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "regions")
class RegionEntity (
    @PrimaryKey
    val regionId: Int,
    @ColumnInfo(name = "regionName")
    val regionName: String
)
