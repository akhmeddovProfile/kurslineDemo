package com.example.kurslinemobileapp.service.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kurslinemobileapp.service.Room.category.CategoryDao
import com.example.kurslinemobileapp.service.Room.category.CategoryEntity
import com.example.kurslinemobileapp.service.Room.category.SubCategoryDao
import com.example.kurslinemobileapp.service.Room.category.SubCategoryEntity
import com.example.kurslinemobileapp.service.Room.mode.ModeDao
import com.example.kurslinemobileapp.service.Room.mode.ModeEntity
import com.example.kurslinemobileapp.service.Room.region.RegionDao
import com.example.kurslinemobileapp.service.Room.region.RegionEntity

@Database(entities = [RegionEntity::class, ModeEntity::class,CategoryEntity::class,SubCategoryEntity::class], version = 4)
abstract class AppDatabase :RoomDatabase(){
    abstract fun regionDao(): RegionDao
    abstract fun modeDao(): ModeDao
    abstract fun categoryDao(): CategoryDao
    abstract fun subCategoryDao(): SubCategoryDao

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