package com.example.kurslinemobileapp.service.Room.category

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "subCategory")
class SubCategoryEntity (
    @PrimaryKey val subCategoryId: Int,
    val subCategoryName: String,
    val categoryId: Int
        )