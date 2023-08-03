package com.example.kurslinemobileapp.service.Room.region

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RegionDao {
    @Query("SELECT * FROM regions")
    fun getAllRegions(): kotlinx.coroutines.flow.Flow<List<RegionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(regions: List<RegionEntity>)

    @Query("SELECT * FROM regions where regionId=:regionId")
    suspend fun getRegionsForEditAnnouncement(regionId:Int):RegionEntity
}