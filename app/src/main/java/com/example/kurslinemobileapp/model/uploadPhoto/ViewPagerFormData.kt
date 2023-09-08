package com.example.kurslinemobileapp.model.uploadPhoto

import android.graphics.Bitmap

data class ViewPagerFormData (
    val imagePath: String,
    val compressedBitmap: Bitmap?
)