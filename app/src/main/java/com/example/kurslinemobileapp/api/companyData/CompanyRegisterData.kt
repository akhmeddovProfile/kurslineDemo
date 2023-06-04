package com.example.kurslinemobileapp.api.companyData

data class CompanyRegisterData(
    val categories: List<Category>,
    val isOnlines: List<IsOnline>,
    val regions: List<Region>,
    val statuses: List<Statuse>
)