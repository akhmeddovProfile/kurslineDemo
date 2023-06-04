package com.example.kurslinemobileapp.api.companyData

data class Category(
    val categoryId: Int,
    val categoryName: String,
    val companies: List<Any>,
    val subCategories: List<SubCategory>
)