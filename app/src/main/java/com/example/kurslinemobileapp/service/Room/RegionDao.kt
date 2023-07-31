package com.example.kurslinemobileapp.service.Room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.util.concurrent.Flow

@Dao
interface RegionDao {
    @Query("SELECT * FROM regions")
    fun getAllRegions(): kotlinx.coroutines.flow.Flow<List<RegionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(regions: List<RegionEntity>)
}