package com.example.kurslinemobileapp.view.loginRegister

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.api.register.RegisterAPI
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.Constant.sharedkeyname
import com.example.kurslinemobileapp.service.RetrofitService
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_register_company.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException

class RegisterCompanyActivity : AppCompatActivity() {

    var compositeDisposable = CompositeDisposable()
    //local data save
    private  var block : Boolean  =true
    lateinit var editor: SharedPreferences.Editor
    private lateinit var sharedPreferences: SharedPreferences

    //Image
    var selectedPicture : Uri? = null
    var selectedBitmap : Bitmap? = null
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    //Variable
    lateinit var name:String
    lateinit var companyEmail:String
    lateinit var companyPassword:String
    lateinit var companyFullName:String
    lateinit var companyRegion:String
    lateinit var companyAddress:String
    lateinit var companyPhone:String
    lateinit var companyMode:String
    lateinit var companyStatus:String
    lateinit var companyCategory:String
    lateinit var aboutCompany:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_company)

        sharedPreferences=getSharedPreferences(sharedkeyname,Context.MODE_PRIVATE)
        editor=sharedPreferences.edit()

        createBusinessAccountBtn.setOnClickListener {
            block=true
            val companyNameContainer = nameEditText.text.toString().trim()
            val companyEmailContainer = companyEmailEditText.text.toString().trim()
            val companyPasswordContainer = companyPasswordEdit.text.toString().trim()
            val companyFullNameContainer = companyFullNameEditText.text.toString().trim()
            val companyRegionContainer = companyRegionEditText.text.toString().trim()
            val companyAddressContainer = companyAdressEditText.text.toString().trim()
            val companyPhoneContainer = companyPhoneEditText.text.toString().trim()
            val companyModeContainer = companyModeEditText.text.toString().trim()
            val companyStatusContainer = compantStatusEditText.text.toString().trim()
            val companyCategoryContainer = companyCategoryEditText.text.toString().trim()
            val aboutCompanyContainer = aboutCompanyEditText.text.toString().trim()

            if (companyNameContainer.isEmpty()){
                nameEditText.error=" Name required"
                nameEditText.requestFocus()
                block=false
            }
            if (companyEmailContainer.isEmpty()){
                companyEmailEditText.error="Email required"
                companyEmailEditText.requestFocus()
                block=false
            }
            if (companyPasswordContainer.isEmpty()){
                companyPasswordEdit.error="Password required"
                companyPasswordEdit.requestFocus()
                block=false
            }
            if (companyFullNameContainer.isEmpty()){
                companyFullNameEditText.error="Company Name required"
                companyFullNameEditText.requestFocus()
                block=false
            }
            if (companyRegionContainer.isEmpty()){
                companyRegionEditText.error="Region required"
                companyRegionEditText.requestFocus()
                block=false
            }

            if (companyAddressContainer.isEmpty()){
                companyAdressEditText.error="Address required"
                companyAdressEditText.requestFocus()
                block=false
            }

            if (companyPhoneContainer.isEmpty()){
                companyPhoneEditText.error="Phone required"
                companyPhoneEditText.requestFocus()
                block=false
            }
            if (companyModeContainer.isEmpty()){
                companyModeEditText.error="Mode required"
                companyModeEditText.requestFocus()
                block=false
            }
            if (companyStatusContainer.isEmpty()){
                compantStatusEditText.error="Status required"
                compantStatusEditText.requestFocus()
                block=false
            }
            if (companyCategoryContainer.isEmpty()){
                companyCategoryEditText.error="Category required"
                companyCategoryEditText.requestFocus()
                block=false
            }
            if (aboutCompanyContainer.isEmpty()){
                aboutCompanyEditText.error="About Company required"
                aboutCompanyEditText.requestFocus()
                block=false
            }



        }

        downloadPhotoFromGalery()

        companyPhoto.setOnClickListener {
            selectCertificate(it)
        }


    }


    fun sendCompanydata(
        companyCategoryId: String,
        companyAbout: String,
        companyAddress: String,
        companyName: String,
        gender: String,
        password: String,
        mobileNumber: String,
        email: String,
        userFullName: String,
        imagePath: String
    )

    {
        val file=File(imagePath)
        val reqFile:RequestBody=RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val photos:MultipartBody.Part=MultipartBody.Part.createFormData("photos",file.name,reqFile)

        val username:RequestBody=RequestBody.create("text/plain".toMediaTypeOrNull(), userFullName)
        val companyemail:RequestBody=RequestBody.create("text/plain".toMediaTypeOrNull(), email)
        val companyNumber:RequestBody=RequestBody.create("text/plain".toMediaTypeOrNull(), mobileNumber)
        val companyPassword:RequestBody=RequestBody.create("text/plain".toMediaTypeOrNull(), password)
        val companyGender:RequestBody=RequestBody.create("text/plain".toMediaTypeOrNull(), gender)
        val address:RequestBody=RequestBody.create("text/plain".toMediaTypeOrNull(), companyAddress)
        val name:RequestBody=RequestBody.create("text/plain".toMediaTypeOrNull(), companyName)
        val about:RequestBody=RequestBody.create("text/plain".toMediaTypeOrNull(), companyAbout)
        val categoryid:RequestBody=RequestBody.create("text/plain".toMediaTypeOrNull(), companyCategoryId)

     /*   compositeDisposable = CompositeDisposable()
        val retrofit =
            RetrofitService(Constant.BASE_URL).retrofit.create(RegisterAPI::class.java)
        *//*val request = RegisterCompanyRequest()*//*
        retrofit.createCompany(username,companyemail, companyNumber, companyPassword, companyGender, address,name,categoryid,about,photos)

        compositeDisposable.add(
            retrofit.createCompany(username,companyemail, companyNumber, companyPassword, companyGender, address,name,categoryid,about,photos)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse,
                    { throwable ->
                        println(throwable) })
        )*/


    }



     fun selectCertificate(view: View){

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",
                    View.OnClickListener {
                        permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    }).show()
            } else {
                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else {
            val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        }
    }

     fun downloadPhotoFromGalery() {
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intentFromResult = result.data
                if (intentFromResult != null) {
                    selectedPicture = intentFromResult.data
                    try {
                        if (Build.VERSION.SDK_INT >= 28) {
                            val source = ImageDecoder.createSource(
                                this.contentResolver,
                                selectedPicture!!
                            )
                            selectedBitmap = ImageDecoder.decodeBitmap(source)

                            certificateImage.setImageBitmap(selectedBitmap)
                        } else {
                            selectedBitmap = MediaStore.Images.Media.getBitmap(
                                this.contentResolver,
                                selectedPicture
                            )
                            certificateImage.setImageBitmap(selectedBitmap)
                        }
                        val cervicatePath = selectedPicture?.path
                        companyPhoto.setText(cervicatePath!!.substring((cervicatePath.length - 8) , cervicatePath.length)+".JPG")
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { result ->
            if (result) {
                //permission granted
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else {
                //permission denied
                Toast.makeText(this@RegisterCompanyActivity, "Permisson needed!", Toast.LENGTH_LONG).show()
            }
        }
    }





    override fun onPause() {
        getValues()
        super.onPause()
    }

    private fun getValues(){
        if(nameEditText.text?.isEmpty()!!){
            name=nameEditText.text.toString().trim()
            editor.putString("companyName",name).apply()
        }
        if(companyEmailEditText.text?.isEmpty()!!){
            companyEmail=companyEmailEditText.text.toString().trim()
            editor.putString("companyEmail",companyEmail).apply()
        }
        if(companyPasswordEdit.text?.isEmpty()!!){
            companyPassword=companyPasswordEdit.text.toString().trim()
            editor.putString("companyPassword",companyPassword).apply()
        }
        if(companyFullNameEditText.text?.isEmpty()!!){
            companyFullName=companyFullNameEditText.text.toString().trim()
            editor.putString("companyFullname",companyFullName).apply()
        }
        if(companyRegionEditText.text?.isEmpty()!!){
            companyRegion=companyRegionEditText.text.toString().trim()
            editor.putString("companyRegion",companyRegion).apply()
        }
        if (companyAdressEditText.text?.isEmpty()!!){
            companyAddress=companyAdressEditText.text.toString().trim()
            editor.putString("companyAddress",companyAddress).apply()
        }
        if (companyPhoneEditText.text?.isEmpty()!!){
            companyPhone=companyPhoneEditText.text.toString().trim()
            editor.putString("companyPhone",companyPhone).apply()
        }
        if (companyModeEditText.text?.isEmpty()!!){
            companyMode=companyModeEditText.text.toString().trim()
            editor.putString("companyMode",companyMode).apply()
        }
        if (compantStatusEditText.text?.isEmpty()!!){
            companyStatus=compantStatusEditText.text.toString().trim()
            editor.putString("companyStatus",companyStatus).apply()
        }
        if (companyCategoryEditText.text?.isEmpty()!!){
            companyCategory=companyCategoryEditText.text.toString().trim()
            editor.putString("companyCategory",companyCategory).apply()
        }
        if (aboutCompanyEditText.text?.isEmpty()!!){
            aboutCompany=aboutCompanyEditText.text.toString().trim()
            editor.putString("aboutCompany",aboutCompany).apply()
        }

        editor.commit()

    }

}