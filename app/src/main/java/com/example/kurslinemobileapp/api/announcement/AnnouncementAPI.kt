package com.example.kurslinemobileapp.api.announcement

import com.example.kurslinemobileapp.api.announcement.createAnnouncement.CreateAnnouncementRequest
import com.example.kurslinemobileapp.api.announcement.filterAnnouncements.FilterModel
import com.example.kurslinemobileapp.api.announcement.getDetailAnnouncement.AnnouncementDetailModel
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.GetAllAnnouncement
import com.example.kurslinemobileapp.api.announcement.updateanddelete.DeleteAnnouncementResponse
import com.example.kurslinemobileapp.api.announcement.updateanddelete.GetAnnouncementResponse
import com.example.kurslinemobileapp.api.announcement.updateanddelete.GetUserAnn
import com.example.kurslinemobileapp.api.announcement.updateanddelete.UpdateAnnouncementResponse
import com.example.kurslinemobileapp.api.companyData.CompanyRegisterData
import com.example.kurslinemobileapp.api.getUserCmpDatas.companyAnnouncement.CompanyTransactionAnnouncement
import com.example.kurslinemobileapp.model.HiglightNew.HiglightModel
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
    fun getAnnouncement(@Query("limit") limit: Int, @Query("offset") offset: Int) : Observable<GetAllAnnouncement>

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
    fun getAnnouncementFavoriteForUserId(@Query("userId") userId: Int,@Query("limit")limit:Int,@Query("offset")offset:Int) : Observable<GetAllAnnouncement>



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


    @GET("GetAnnouncementForUserById/{userId}/{announcementId}")
    fun getAnnouncementForUpdate(
        @Path("userId") userId: Int,@Path("announcementId") announcementId:Int, @Header("Authorization") token: String
    ):Observable<GetAnnouncementResponse>
    @POST("PutMobileAnnouncement/{userId}/{announcementId}")
    fun updateAnnJSON(
        @Header("Authorization")token:String,
        @Path("userId")userId:Int,
        @Path("announcementId")announcementId:Int,
        @Body createAnnouncementRequest: CreateAnnouncementRequest
    ):Observable<UpdateAnnouncementResponse>

    @Multipart
    @POST("PutAnnouncement/{userId}/{announcementId}")
    fun updateAnnouncementFormData(
        @Part("AnnouncementName")AnnouncementName:RequestBody,
        @Part("AnnouncementDesc")AnnouncementDesc:RequestBody,
        @Part("AnnouncementPrice")AnnouncementPrice:RequestBody,
        @Part("AnnouncementAddress")AnnouncementAddress:RequestBody,
        @Part("AnnouncementRegionId")AnnouncementRegionId:RequestBody,
        @Part("AnnouncementSubCategoryId")AnnouncementSubCategoryId:RequestBody,
        @Part Img:List<MultipartBody.Part>,
        @Part("Teacher")Teacher:RequestBody,
        @Part("AnnouncementIsOnlineId")AnnouncementIsOnlineId:RequestBody,
        @Part("AnnouncementCategoryId")AnnouncementCategoryId:RequestBody,
        @Header("Authorization")token:String,
        @Path("userId")userId:Int,
        @Path("announcementId")announcementId:Int
    ):Observable<UpdateAnnouncementResponse>

    @GET("GetHighLights")
    fun getHighlight(): Observable<HiglightModel>


    @GET("GetNewAnnouncments")
    fun getNewHiglight():Observable<CompanyTransactionAnnouncement>

    @GET("GetVipAnnouncments")
    fun getVipHiglight():Observable<CompanyTransactionAnnouncement>

    @GET("GetMostViewedAnnouncments")
    fun getMostViewHiglight():Observable<CompanyTransactionAnnouncement>
}