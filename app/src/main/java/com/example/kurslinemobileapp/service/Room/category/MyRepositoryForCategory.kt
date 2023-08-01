package com.example.kurslinemobileapp.service.Room.category

import kotlinx.coroutines.flow.Flow

class MyRepositoryForCategory (private val categoryDao: CategoryDao, private val subCategoryDao: SubCategoryDao) {

    fun saveCategories(categories: List<CategoryEntity>) {
        val categoryEntities = categories.map { category ->
            CategoryEntity(category.categoryId, category.categoryName)
        }
        categoryDao.insertAll(categoryEntities)
    }

    fun saveSubCategories(subCategories: List<SubCategoryEntity>) {
        subCategoryDao.insertAll(subCategories)
    }

    fun getAllCategories(): Flow<List<CategoryWithSubCategory>> {
        return categoryDao.getAllCategories()
    }

    fun getSubCategoriesByCategoryId(categoryId: Int): List<SubCategoryEntity> {
        return subCategoryDao.getSubCategoriesByCategoryId(categoryId)
    }
}