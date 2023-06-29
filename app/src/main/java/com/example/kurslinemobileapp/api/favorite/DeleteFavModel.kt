package com.example.kurslinemobileapp.api.favorite

data class DeleteFavModel(
    var productId:Int,
    var unliked:Boolean=true
)
