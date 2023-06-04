package com.example.kurslinemobileapp.api.companyData

import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET

interface CompanyDatasAPI {
    @GET("GetRegisterDatas/")
    fun getCategories(): Observable<CompanyRegisterData>
    @GET("GetRegisterDatas/")
    fun getRegions(): Observable<CompanyRegisterData>
}