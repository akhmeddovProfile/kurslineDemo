package com.example.kurslinemobileapp.view.loginRegister

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kurslinemobileapp.R
import kotlinx.android.synthetic.main.activity_register_company.*

class RegisterCompanyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_company)

        createBusinessAccountBtn.setOnClickListener {
            val companyNameContainer = companyFullNameEditText.text.toString()
            val companyEmailContainer = companyEmailEditText.text.toString()
            val companyPasswordContainer = companyPasswordEdit.text.toString()
            val companyFullNameContainer = companyFullNameEditText.text.toString()
            val companyRegionContainer = companyRegionEditText.text.toString()
            val companyAddressContainer = companyAdressEditText.text.toString()
            val companyPhoneContainer = companyPhoneEditText.text.toString()
            val companyModeContainer = companyModeEditText.text.toString()
            val companyStatusContainer = compantStatusEditText.text.toString()
            val companyCategoryContainer = companyCategoryEditText.text.toString()
            val aboutCompanyContainer = aboutCompanyEditText.text.toString()
        }

    }

    private fun downloadPhotoFromGalery() {

    }

}