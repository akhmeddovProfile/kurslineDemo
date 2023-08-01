package com.example.kurslinemobileapp.service.Room.category

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "categories")
 class CategoryEntity (
    @PrimaryKey val categoryId: Int,
    val categoryName: String
        )