package com.example.kurslinemobileapp.model.mainpage

import android.widget.ImageView

data class ProductDetail(
    val product: List<Product>,
    val price: String,
    val catagory: String,
    val region: String,
    val teacherNames: List<String>,
    val heartButton: ImageView

)
