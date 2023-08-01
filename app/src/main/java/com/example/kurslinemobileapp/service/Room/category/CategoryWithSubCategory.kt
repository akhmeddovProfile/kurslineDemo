package com.example.kurslinemobileapp.service.Room.category

import androidx.room.Embedded
import androidx.room.Relation

data class CategoryWithSubCategory (
    @Embedded val category: CategoryEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryId"
    )
    val subCategories: List<SubCategoryEntity>
)
