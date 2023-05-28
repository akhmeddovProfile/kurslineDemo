package com.example.kurslinemobileapp.view.loginRegister

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.kurslinemobileapp.R
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
                                requireContext().contentResolver,
                                selectedPicture!!
                            )
                            selectedBitmap = ImageDecoder.decodeBitmap(source)
                            requireView().certificateImage.setImageBitmap(selectedBitmap)
                        } else {
                            selectedBitmap = MediaStore.Images.Media.getBitmap(
                                requireContext().contentResolver,
                                selected
                            )
                            requireView().certificateImage.setImageBitmap(selectedBitmap)
                        }
                        val cervicatePath = selectedPicture?.path
                        lawyerCertificate.setText(cervicatePath!!.substring((cervicatePath.length - 8) , cervicatePath.length)+".JPG")
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        permissionLauncherCertificate = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { result ->
            if (result) {
                //permission granted
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncherCertificate.launch(intentToGallery)
            } else {
                //permission denied
                Toast.makeText(requireContext(), "Permisson needed!", Toast.LENGTH_LONG).show()
            }
        }
    }

}