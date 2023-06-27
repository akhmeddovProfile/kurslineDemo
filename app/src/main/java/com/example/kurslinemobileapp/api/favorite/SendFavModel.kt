package com.example.kurslinemobileapp.api.favorite

data class SendFavModel(
    var userid:Int,
    var productid : Int,
    var isSelected:Boolean=false
)