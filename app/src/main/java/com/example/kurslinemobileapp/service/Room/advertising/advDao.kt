package com.app.kurslinemobileapp.service.Room.advertising

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kurslinemobileapp.service.Room.advertising.advEntity


@Dao
interface advDao {
    @Query("SELECT * FROM adv")
    fun getAllAdv(): kotlinx.coroutines.flow.Flow<List<advEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAdv(regions: List<advEntity>)
}