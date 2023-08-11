package com.example.kurslinemobileapp.service.Room.courses

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "course")
class CourseEntity (
    @PrimaryKey
    val courseId: Int,
    @ColumnInfo(name = "modeName")
    val courseName: String
        )