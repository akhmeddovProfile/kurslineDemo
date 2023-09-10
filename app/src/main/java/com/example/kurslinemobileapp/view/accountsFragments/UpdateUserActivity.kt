package com.example.kurslinemobileapp.view.accountsFragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.InputFilter
import android.view.View
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.app.kurslinemobileapp.R
import com.example.kurslinemobileapp.api.updateUserCompany.UpdateAPI
import com.example.kurslinemobileapp.api.updateUserCompany.UpdateResponse
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_update_user.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class UpdateUserActivity : AppCompatActivity() {
    private var block: Boolean = true
    private lateinit var compositeDisposable: CompositeDisposable
    private val REQUEST_IMAGE_CAPTURE = 1 // Request code for image capture
    val MAX_IMAGE_WIDTH = 800 // Maximum width for the compressed image
    val MAX_IMAGE_HEIGHT = 600 // Maximum height for the compressed image

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            val result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openGallery()
        } else {

            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingInflatedId", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_user)

        val scroll = findViewById<ScrollView>(R.id.scrollUserUpdateAccount)
        scroll.visibility = View.VISIBLE
        val lottie = findViewById<LottieAnimationView>(R.id.loadingUserUpdateAccount)
        lottie.visibility = View.GONE
        lottie.pauseAnimation()

        val sharedPreferences = this.getSharedPreferences(Constant.sharedkeyname, Context.MODE_PRIVATE)
        val id = sharedPreferences.getInt("userID", 0)
        val token = sharedPreferences.getString("USERTOKENNN", "")
        val authHeader = "Bearer $token"
        println("userID" + id)
        println("userToken" + authHeader)

        val accountName = sharedPreferences.getString("accountName", "")
        val accountPhone = sharedPreferences.getString("accountPhone", "")
        val accountMail = sharedPreferences.getString("accountMail", "")
          val accountPhoto = sharedPreferences.getString("profilePhotoUrl", "")

        updateAccountNameEditText.setText(accountName)
        updateAccountPhoneEditText.setText(accountPhone)
        updateAccountMailEditText.setText(accountMail)
       // photoUrlEditText.setText(accountPhoto)

        // Load the image using Picasso into the circular ImageView

        Picasso.get().load(accountPhoto).into(myUserUpdateProfileImage)


        myUserUpdateProfileImage.setOnClickListener {
            if(!checkPermission()){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    checkAndRequestPermissions()
                }
                /*       val miuiVersion = BuildProperties.getMIUIVersion()
                       if (miuiVersion == "14.0.3") {
                           showMIUIExplanationDialog() // Show a custom explanation
                       }*/
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE) // Launch permission request directly
            }
            else{
                openGallery()
            }
        }



        savedUpdatesBtn.setOnClickListener {
            block = true
            val name = updateAccountNameEditText.text.toString().trim()
            val email = updateAccountMailEditText.text.toString().trim()
            val phone =updateAccountPhoneEditText.text.toString().trim()
            updateAccountPhoneEditText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(13))
            if(name.isEmpty()){
                updateAccountNameEditText.requestFocus()
                updateAccountNameEditText.error = "Full name is not be empty"
                block  = false
            }
            if(email.isEmpty()){
                updateAccountMailEditText.requestFocus()
                updateAccountMailEditText.error = "Email address is not be empty"
                block  = false
            }
            if(phone.isEmpty()){
                updateAccountPhoneEditText.requestFocus()
                updateAccountPhoneEditText.error ="Phone is not be empty"
                block  = false
            }

            showProgressButton(true)
            val imageUrl = if (photoUrlEditText.text.toString().isNotEmpty() )
            {
                photoUrlEditText.text.toString().trim()
            } else {
                null
            }

            val userName = if(  updateAccountNameEditText.text.toString().isNotEmpty()){
                updateAccountNameEditText.text.toString().trim()
            } else {
                updateAccountNameEditText.text.toString().trim()
            }

            val userPhone = if(  updateAccountPhoneEditText.text.toString().isNotEmpty()){
                updateAccountPhoneEditText.text.toString().trim()
            } else {
                updateAccountPhoneEditText.text.toString().trim()
            }

            val userMail = if(  updateAccountMailEditText.text.toString().isNotEmpty()){
                updateAccountMailEditText.text.toString().trim()
            } else {
                updateAccountMailEditText.text.toString().trim()
            }

            updateUser(userName, userMail, userPhone, 1, imageUrl, authHeader, id)
        }

    }

    private fun updateUser(
        userName: String,
        userEmail: String,
        userPhone: String,
        userGender: Int,
        imagePath: String?,
        token: String,
        userId: Int
    ) {
        val name: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), userName)
        val mail: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), userEmail)
        val phone: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), userPhone)
        val gender: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), userGender.toString())

        val retrofit = RetrofitService(Constant.BASE_URL).retrofit.create(UpdateAPI::class.java)

        val photo: MultipartBody.Part? = if (imagePath != null) {
            val file = File(imagePath)
            val reqFile: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            MultipartBody.Part.createFormData("Photos", file.name, reqFile)
        } else {
            null
        }

        compositeDisposable = CompositeDisposable()

        compositeDisposable.add(
            retrofit.userUpdateMethod(name, mail, phone, gender, photo, token, userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    this::handleResponseUpdate,
                    { throwable ->
                        println(throwable.message)
                        if (throwable.message!!.contains("HTTP 409")){
                            Toast.makeText(this,getString(R.string.http409String),Toast.LENGTH_SHORT).show()
                        }else{
                            val text = getString(R.string.infosWrong)
                            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
                        }
                        showProgressButton(false)
                        println(throwable)
                    }
                )
        )
    }


    @SuppressLint("ServiceCast")
    private fun handleResponseUpdate(response: UpdateResponse) {
        println("Response: " + response.isSuccess)
        Toast.makeText(this,getString(R.string.saveSuccesfull),Toast.LENGTH_SHORT).show()
         onBackPressed()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Permission is already granted, proceed with accessing gallery
                openGallery()
            } else {
                // Request permission
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            showPermissionMessageForOlderDevices()
            // Handle devices with API level lower than 31
            // You might want to show a message or handle it differently
        }
    }

    private fun showPermissionMessageForOlderDevices() {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("To access the gallery, you need to grant storage permission.")
            .setPositiveButton("Grant Permission") { dialog, _ ->
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, Constant.PICK_IMAGE_REQUEST)
    }

    private fun compressImageFile(imagePath: String): Bitmap? {
        val file = File(imagePath)
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(file.absolutePath, options)
        val imageWidth = options.outWidth
        val imageHeight = options.outHeight
        val scaleFactor = Math.min(imageWidth / MAX_IMAGE_WIDTH, imageHeight / MAX_IMAGE_HEIGHT)
        options.inJustDecodeBounds = false
        options.inSampleSize = scaleFactor
        return BitmapFactory.decodeFile(file.absolutePath, options)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data?.data
            val imagePath = selectedImageUri?.let { getRealPathFromURI(it) }
            if (imagePath != null) {
                val compressedBitmap = compressImageFile(imagePath)
                photoUrlEditText.setText(imagePath)
                myUserUpdateProfileImage.setImageBitmap(compressedBitmap)
                if(compressedBitmap!=null){
                    val compressedImagePath = saveCompressedBitmapToFile(compressedBitmap)
                    photoUrlEditText.setText(compressedImagePath)
                    println("CompressedImagePath"+compressedImagePath)
                }
                println(imagePath)
            }
        }
    }
    private fun saveCompressedBitmapToFile(bitmap: Bitmap): String? {
        val outputDir = this?.cacheDir // Get the directory to store the compressed image
        val outputFile = File.createTempFile("compressed_", ".jpg", outputDir)
        var outputStream: FileOutputStream? = null
        try {
            outputStream = FileOutputStream(outputFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream) // Compress and save the bitmap as JPEG with 80% quality
            return outputFile.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            outputStream?.close()
        }
        return null
    }
    private fun getFileSize(uri: Uri): Long {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                if (sizeIndex != -1) {
                    return it.getLong(sizeIndex)
                }
            }
        }
        return 0
    }
    private fun getRealPathFromURI(uri: Uri): String? {
        var path: String? = null
        val imageSize = getFileSize(uri) // Get the image size in bytes
        val maxSizeBytes = 3 * 1024 * 1024
        if (imageSize > maxSizeBytes) {
            // Show a toast message indicating that the image size is too large
            Toast.makeText(this, "Image size is too large (Max: 3MB)", Toast.LENGTH_SHORT).show()
            return null// Don't proceed with processing the image
        }
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                path = it.getString(columnIndex)
            }
        }
        return path
    }

    private fun showProgressButton(show: Boolean) {
        if (show) {
            savedUpdatesBtn.apply {
                isEnabled = false
                text = getString(R.string.savingChange)  // Set empty text or loading indicator text
                // Add loading indicator drawable or ProgressBar if needed
            }
        } else {
            savedUpdatesBtn.apply {
                isEnabled = true
                text = getString(R.string.saveChange)
                // Restore original background, text color, etc., if modified
            }
        }
    }
}