package com.example.kurslinemobileapp.view.accountsFragments

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.service.Constant
import com.squareup.picasso.Picasso
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_company_update.*
import kotlinx.android.synthetic.main.activity_update_user.*

class CompanyUpdateActivity : AppCompatActivity() {
    private lateinit var compositeDisposable: CompositeDisposable
    private val REQUEST_IMAGE_CAPTURE = 1 // Request code for image capture
    val MAX_IMAGE_WIDTH = 800 // Maximum width for the compressed image
    val MAX_IMAGE_HEIGHT = 600 // Maximum height for the compressed image
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_update)

        val sharedPreferences = this.getSharedPreferences(Constant.sharedkeyname, Context.MODE_PRIVATE)
        val id = sharedPreferences.getInt("userID", 0)
        val token = sharedPreferences.getString("USERTOKENNN", "")
        val authHeader = "Bearer $token"
        println("userID" + id)
        println("userToken" + authHeader)

        val accountName = sharedPreferences.getString("companyAccountName", "")
        val accountPhone = sharedPreferences.getString("companyAccountPhone", "")
        val accountMail = sharedPreferences.getString("companyAccountMail", "")
        val accountNameCompany = sharedPreferences.getString("companyName", "")
        val accountAddress = sharedPreferences.getString("companyAddress", "")
        val accountCategory = sharedPreferences.getString("companyCategory", "")
        val accountStatus = sharedPreferences.getString("companyStatus", "")
        val accountAbout = sharedPreferences.getString("companyAbout", "")
        val accountPhoto = sharedPreferences.getString("companyPhotoUrl", "")

        businessAccountUpdateNameEditText.setText(accountName)
        businessAccountUpdatePhoneEditText.setText(accountPhone)
        businessAccountUpdateEmailEditText.setText(accountMail)
        businessAccountUpdateCompanyEditText.setText(accountNameCompany)
        businessAccountRegionEditText.setText(accountCategory)
        companyUpdateStatusEditText.setText(accountStatus)
        companyUpdateAdressEditText.setText(accountAddress)
        businessAccountAboutEditText.setText(accountAbout)
        // photoUrlEditText.setText(accountPhoto)

        // Load the image using Picasso into the circular ImageView

//        Picasso.get().load(accountPhoto).into(myCompanyUpdateProfilePhoto)
    }
}