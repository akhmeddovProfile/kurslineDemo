package com.example.kurslinemobileapp.service.Room.tutors

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kurslinemobileapp.service.Room.courses.CourseEntity

@Dao
interface TutorsDao {
    @Query("SELECT * FROM tutors")
    fun getAlltutors(): kotlinx.coroutines.flow.Flow<List<TutorsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlltutors(regions: List<TutorsEntity>)
}