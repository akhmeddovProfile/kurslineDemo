package com.example.kurslinemobileapp.view.loginRegister

import android.app.Activity
import android.content.Intent
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
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_register_company.*
import java.io.IOException

class RegisterCompanyActivity : AppCompatActivity() {
    //Image
    var selectedPicture : Uri? = null
    var selectedBitmap : Bitmap? = null
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
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
        downloadPhotoFromGalery()

        companyPhoto.setOnClickListener {
            selectCertificate(it)
        }
    }


    private fun selectCertificate(view: View){

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
    private fun downloadPhotoFromGalery() {
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

}