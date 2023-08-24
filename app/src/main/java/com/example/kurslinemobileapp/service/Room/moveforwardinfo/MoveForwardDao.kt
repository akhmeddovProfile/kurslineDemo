package com.example.kurslinemobileapp.service.Room.moveforwardinfo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MoveForwardDao {
    @Query("SELECT * FROM moveforwardInfo")
    fun getAllMoveForwardPrice()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllMoveForwardPrice()
}