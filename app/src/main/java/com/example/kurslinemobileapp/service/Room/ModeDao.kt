package com.example.kurslinemobileapp.service.Room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface ModeDao {
    @Query("SELECT * FROM mode")
    fun getAllMode(): kotlinx.coroutines.flow.Flow<List<ModeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(regions: List<ModeEntity>)
}