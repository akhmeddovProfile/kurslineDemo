package com.example.kurslinemobileapp.api.companyTeachers

import com.example.kurslinemobileapp.api.companyData.CompanyRegisterData
import io.reactivex.Observable
import retrofit2.http.GET

interface CompanyTeacherAPI {
    @GET("GetCompanies")
    fun getCompanies(): Observable<CompanyTeacherModel>
}