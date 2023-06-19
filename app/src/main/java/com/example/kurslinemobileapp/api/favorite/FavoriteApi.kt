package com.example.kurslinemobileapp.api.favorite

import io.reactivex.Observable
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface FavoriteApi {

    @POST("PostFavorite/{userId}/{announcementId}")
    fun postFavorite(@Header("Authorization")token:String,
                     @Path("userId")userId:Int,
                     @Path("announcementId")announcementId:Int
    ):Observable<FavoriteResponse>

    @POST("PostFavorite/{userId}/{announcementId}")
    fun deleteFavorite(@Header("Authorization")token:String,
                     @Path("userId")userId:Int,
                     @Path("announcementId")announcementId:Int
    ):Observable<FavoriteResponse>
}