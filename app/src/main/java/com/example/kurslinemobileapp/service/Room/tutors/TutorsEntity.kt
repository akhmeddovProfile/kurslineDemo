package com.example.kurslinemobileapp.service.Room.tutors

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "tutors")
class TutorsEntity (
    @PrimaryKey
    val tutorsId:Int,
    @ColumnInfo
    val tutorsName:String
)

