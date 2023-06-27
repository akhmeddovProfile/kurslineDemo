package com.example.kurslinemobileapp.api.comment

import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface CommentAPI {
    @POST("PostComment/{userId}/{announcementId}")
    fun postComment(@Header("Authorization") token: String, @Path("userId") userId: Int,
                    @Path("announcementId") announcementId:Int,@Body commentRequest: CommentRequest):
            Observable<CommentResponse>
}