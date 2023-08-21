package com.example.kurslinemobileapp.api.announcement

import com.example.kurslinemobileapp.api.announcement.createAnnouncement.CreateAnnouncementRequest
import com.example.kurslinemobileapp.api.announcement.filterAnnouncements.FilterModel
import com.example.kurslinemobileapp.api.announcement.getDetailAnnouncement.AnnouncementDetailModel
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.GetAllAnnouncement
import com.example.kurslinemobileapp.api.announcement.updateanddelete.DeleteAnnouncementResponse
import com.example.kurslinemobileapp.api.announcement.updateanddelete.GetUserAnn
import com.example.kurslinemobileapp.api.announcement.updateanddelete.UpdateAnnouncementResponse
import io.reactivex.Observable
import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface AnnouncementAPI {
    @GET("GetAnnouncements")
    fun getAnnouncement() : Observable<GetAllAnnouncement>

    @GET("GetAnnouncementById/{annouuncementId}/{userId}")
    fun getDataById(@Path("annouuncementId") id: Int,@Path("userId")userId: Int): Observable<AnnouncementDetailModel>

    @GET("GetAnnouncementForUserById/{userId}/{announcementId}")
    fun getAnnouncementForUser(@Path("userId") userId: Int,@Path("announcementId") announcementId:Int, @Header("Authorization") token: String)
            : Observable<GetUserAnn>

    @GET("GetAnnouncements")
    fun getFilterProducts(@Query("limit") limit: Int,
                          @Query("offset") offset: Int,
                          @Query("RegionId") regionId: String,
                          @Query("CategoryId") categoryId: String,
                          @Query("search") search: String,
                          @Query("minPrice") minPrice: String,
                          @Query("maxPrice") maxPrice: String,
                          @Query("statusId") statusId: String,
                          @Query("isOnlineId") isOnlineId: String,
                          @Query("companyId") companyId:String,
                          @Query("userId") userId: Int) : Observable<GetAllAnnouncement>

    @GET("GetAnnouncements")
    fun getAnnouncementFavoriteForUserId(@Query("userId") userId: Int) : Observable<GetAllAnnouncement>

    @POST("PostMessage")
    fun writeUs(
        @Query("tel")tel:String,
        @Query("message")message:String
    ):Deferred<WriteUsResponse>


    @POST("DeleteAnnouncement/{userId}/{announcementId}")
    fun deleteAnnouncementForOwner(
        @Header("Authorization")token:String,
        @Path("userId")userId:Int,
        @Path("announcementId")announcementId:Int
    ):Observable<DeleteAnnouncementResponse>

    @POST("PutMobileAnnouncement/{userId}/{announcementId}")
    fun updateAnnJSON(
        @Header("Authorization")token:String,
        @Path("userId")userId:Int,
        @Path("announcementId")announcementId:Int,
        @Body createAnnouncementRequest: CreateAnnouncementRequest
    ):Observable<UpdateAnnouncementResponse>

}