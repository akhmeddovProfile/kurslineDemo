package com.example.kurslinemobileapp.service.Room.category

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface SubCategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(subCategories: List<SubCategoryEntity>)

    @Query("SELECT * FROM subCategory WHERE categoryId = :categoryId")
    fun getSubCategoriesByCategoryId(categoryId: Int): List<SubCategoryEntity>
}