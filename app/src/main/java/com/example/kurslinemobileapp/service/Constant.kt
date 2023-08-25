package com.example.kurslinemobileapp.service

import androidx.room.Room
import com.example.kurslinemobileapp.service.Room.AppDatabase

object Constant {
    const val BASE_URL = "https://kursline.az/"
    const val BASE_URL_PAYMENT="https://api.payriff.com/api/v2/"
    const val sharedkeyname="MyPrefs"
    const val PICK_IMAGE_REQUEST = 1
    const val PERMISSION_READ_EXTERNAL_STORAGE=1
    var isFavorite:Boolean=false

}