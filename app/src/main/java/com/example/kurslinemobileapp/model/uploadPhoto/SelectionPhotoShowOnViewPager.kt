package com.example.kurslinemobileapp.model.uploadPhoto

import android.net.Uri

data class SelectionPhotoShowOnViewPager(
    val imageName: String?,
    val imageUri: Uri,
    val base64String: String
)