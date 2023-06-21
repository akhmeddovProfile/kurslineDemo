package com.example.kurslinemobileapp.api.companyTeachers

import com.example.kurslinemobileapp.api.companyTeachers.companyProfile.CompanyDetail
import com.example.kurslinemobileapp.api.companyTeachers.companyTeacherRow.CompanyTeacherModel
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface CompanyTeacherAPI {
    @GET("GetCompanies")
    fun getCompanies(): Observable<CompanyTeacherModel>

    @GET("GetCompany/{companyId}")
    fun getCompanyProfile(@Path("companyId") companyId: Int) : Observable<CompanyDetail>
}