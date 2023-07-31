package com.example.kurslinemobileapp.service.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [RegionEntity::class,ModeEntity::class], version = 2)
abstract class AppDatabase :RoomDatabase(){
    abstract fun regionDao():RegionDao
    abstract fun modeDao():ModeDao

    companion object {
/*        val MIGRATION_1_2: androidx.room.migration.Migration = object : androidx.room.migration.Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("")
            }
        }*/
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