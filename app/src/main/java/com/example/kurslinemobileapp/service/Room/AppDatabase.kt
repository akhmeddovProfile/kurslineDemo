package com.example.kurslinemobileapp.service.Room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RegionEntity::class], version = 1)
abstract class AppDatabase :RoomDatabase(){
    abstract fun regionDao():RegionDao
}