package com.example.kurslinemobileapp.service.Room.status

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kurslinemobileapp.service.Room.mode.ModeEntity


@Dao
interface StatusDao {
    @Query("SELECT * FROM status")
    fun getAllMode(): kotlinx.coroutines.flow.Flow<List<StatusEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllStatus(regions: List<StatusEntity>)
}