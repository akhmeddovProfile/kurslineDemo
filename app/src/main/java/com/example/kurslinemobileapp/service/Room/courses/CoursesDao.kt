package com.example.kurslinemobileapp.service.Room.courses

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kurslinemobileapp.service.Room.mode.ModeEntity


@Dao
interface CoursesDao {
    @Query("SELECT * FROM course")
    fun getAllcourse(): kotlinx.coroutines.flow.Flow<List<CourseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllcourse(regions: List<CourseEntity>)
}