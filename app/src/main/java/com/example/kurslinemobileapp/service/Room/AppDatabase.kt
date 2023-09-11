package com.example.kurslinemobileapp.service.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kurslinemobileapp.service.Room.category.CategoryDao
import com.example.kurslinemobileapp.service.Room.category.CategoryEntity
import com.example.kurslinemobileapp.service.Room.category.SubCategoryDao
import com.example.kurslinemobileapp.service.Room.category.SubCategoryEntity
import com.example.kurslinemobileapp.service.Room.courses.CourseEntity
import com.example.kurslinemobileapp.service.Room.courses.CoursesDao
import com.example.kurslinemobileapp.service.Room.mode.ModeDao
import com.example.kurslinemobileapp.service.Room.mode.ModeEntity
import com.example.kurslinemobileapp.service.Room.region.RegionDao
import com.example.kurslinemobileapp.service.Room.region.RegionEntity
import com.example.kurslinemobileapp.service.Room.status.StatusDao
import com.example.kurslinemobileapp.service.Room.status.StatusEntity
import com.example.kurslinemobileapp.service.Room.tutors.TutorsDao
import com.example.kurslinemobileapp.service.Room.tutors.TutorsEntity

@Database(entities = [RegionEntity::class, ModeEntity::class,CategoryEntity::class,SubCategoryEntity::class,StatusEntity::class,CourseEntity::class,TutorsEntity::class], version = 8)
abstract class AppDatabase :RoomDatabase(){
    abstract fun regionDao(): RegionDao
    abstract fun modeDao(): ModeDao
    abstract fun categoryDao(): CategoryDao
    abstract fun subCategoryDao(): SubCategoryDao
    abstract fun statusDao():StatusDao
    abstract fun courseDao():CoursesDao
    abstract fun tutorsDao():TutorsDao
    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app-database"
                )
                    .fallbackToDestructiveMigration() // Add the migration here
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}