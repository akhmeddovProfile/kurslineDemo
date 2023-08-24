package com.example.kurslinemobileapp.service.Room.moveforwardinfo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "moveforwardInfo")
class MoveForwardEntity (
    @PrimaryKey
    val moveForwardId:Int,
    @ColumnInfo(name = "moveForwardDate")
    val moveForwardDate:Int,
    @ColumnInfo(name = "moveForwardCost")
    val moveForwardCost:String
        )