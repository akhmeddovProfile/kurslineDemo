package com.example.kurslinemobileapp.service.Room.category

import androidx.room.*


@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(categories: List<CategoryEntity>)

    @Transaction
    @Query("SELECT * FROM categories")
    fun getAllCategories(): kotlinx.coroutines.flow.Flow<List<CategoryWithSubCategory>>
}