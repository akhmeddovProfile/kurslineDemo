package com.example.kurslinemobileapp.api.favorite

data class SendFavModel(
    var userid:Int,
    var productid : Int,
/*    var userId:Int,
    var token:String,*/

    var isSelected:Boolean=false

)