package com.example.kurslinemobileapp.view.accountsFragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.api.updateUserCompany.UpdateAPI
import com.example.kurslinemobileapp.api.updateUserCompany.UpdateResponse
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.view.MainActivity
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_update_user.*
import kotlinx.android.synthetic.main.activity_user_to_company.*
import kotlinx.android.synthetic.main.fragment_account.view.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class UpdateUserActivity : AppCompatActivity() {
    private lateinit var compositeDisposable: CompositeDisposable
    private val REQUEST_IMAGE_CAPTURE = 1 // Request code for image capture
    val MAX_IMAGE_WIDTH = 800 // Maximum width for the compressed image
    val MAX_IMAGE_HEIGHT = 600 // Maximum height for the compressed image
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_user)

        val scroll = findViewById<ScrollView>(R.id.scrollUserUpdateAccount)
        scroll.visibility = View.VISIBLE
        val lottie = findViewById<LottieAnimationView>(R.id.loadingUserUpdateAccount)
        lottie.visibility = View.GONE
        lottie.pauseAnimation()

        val sharedPreferences =
            this.getSharedPreferences(Constant.sharedkeyname, Context.MODE_PRIVATE)
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
        photoUrlEditText.setText(accountPhoto)

        // Load the image using Picasso into the circular ImageView

        Picasso.get().load(accountPhoto).into(myUserUpdateProfileImage)


        myUserUpdateProfileImage.setOnClickListener {
            launchGalleryIntent()
        }

        val userName = updateAccountNameEditText.text.toString().trim()
        val userPhone = updateAccountPhoneEditText.text.toString().trim()
        val userMail = updateAccountMailEditText.text.toString().trim()
        val imageUrl = photoUrlEditText.text.toString().trim()

        savedUpdatesBtn.setOnClickListener {
            showProgressButton(true)
           updateUser(userName,userMail,userPhone,1,imageUrl,token!!,id)
        }
    }

    private fun updateUser(userName:String,userEmail:String,userPhone:String,userGender:Int,imagePath: String, token:String,userId:Int){
        val file = File(imagePath)
        val reqFile: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val photo: MultipartBody.Part =
            MultipartBody.Part.createFormData("photos", file.name, reqFile)
        val name: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), userName)
        val mail: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), userEmail)
        val phone: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), userPhone)
        val gender: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), userGender.toString())
        compositeDisposable = CompositeDisposable()
        val retrofit =
            RetrofitService(Constant.BASE_URL).retrofit.create(UpdateAPI::class.java)

        compositeDisposable.add(
            retrofit.userUpdateMethod(name,mail,phone,gender,photo,token,userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponseUpdate,
                    { throwable ->
                        val text = "Məlumatlar doğru deyil"
                        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
                        showProgressButton(false)
                        println(throwable)
                    })
        )
    }
    private fun handleResponseUpdate(response: UpdateResponse) {
        println("Response: " + response.isSuccess)
       onBackPressed()
    }

    fun launchGalleryIntent() {
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
    private fun getRealPathFromURI(uri: Uri): String? {
        var path: String? = null
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
                text = "Dəyişiklikləri yadda saxlanılır..."  // Set empty text or loading indicator text
                // Add loading indicator drawable or ProgressBar if needed
            }
        } else {
            savedUpdatesBtn.apply {
                isEnabled = true
                text = "Dəyişiklikləri yadda saxla"
                // Restore original background, text color, etc., if modified
            }
        }
    }
}