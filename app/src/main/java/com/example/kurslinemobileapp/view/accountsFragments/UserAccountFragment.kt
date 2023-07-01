package com.example.kurslinemobileapp.view.accountsFragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ScrollView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.api.getUserCmpDatas.InfoAPI
import com.example.kurslinemobileapp.api.getUserCmpDatas.UserCmpInfoModel.UserInfoModel
import com.example.kurslinemobileapp.api.register.RegisterAPI
import com.example.kurslinemobileapp.api.register.UserRegisterResponse
import com.example.kurslinemobileapp.api.register.UserToCompanyResponse
import com.example.kurslinemobileapp.api.update.UpdateAPI
import com.example.kurslinemobileapp.api.update.UpdateResponse
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.Constant.sharedkeyname
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.view.MainActivity
import com.example.kurslinemobileapp.view.loginRegister.LoginActivity
import com.example.kurslinemobileapp.view.loginRegister.RegisterCompanyActivity
import com.example.kurslinemobileapp.view.loginRegister.UserToCompanyActivity
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_register_company.*
import kotlinx.android.synthetic.main.activity_user_to_company.*
import kotlinx.android.synthetic.main.fragment_account.view.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class UserAccountFragment : Fragment() {
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var view: ViewGroup
    private val REQUEST_IMAGE_CAPTURE = 1 // Request code for image capture
    val MAX_IMAGE_WIDTH = 800 // Maximum width for the compressed image
    val MAX_IMAGE_HEIGHT = 600 // Maximum height for the compressed image

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_account, container, false) as ViewGroup

        val scroll = view.findViewById<ScrollView>(R.id.scrollUserAccount)
        scroll.visibility = View.GONE
        val lottie = view.findViewById<LottieAnimationView>(R.id.loadingUserAccount)
        lottie.visibility = View.VISIBLE
        lottie.playAnimation()
        // Get the SharedPreferences object
        val sharedPreferences =
            requireContext().getSharedPreferences(sharedkeyname, Context.MODE_PRIVATE)
        val id = sharedPreferences.getInt("userID", 0)
        val token = sharedPreferences.getString("USERTOKENNN", "")
        val authHeader = "Bearer $token"
        println("userID" + id)
        println("userToken" + authHeader)
        getDataFromServer(id, authHeader)

        view.goToBusinessCreate.setOnClickListener {
            val intent = Intent(requireContext(), UserToCompanyActivity::class.java)
            startActivity(intent)
        }
        // Display the account information in the UI

        view.backtoMainPage.setOnClickListener {
            //    findNavController().navigate(R.id.action_blankAccountFragment_to_homeFragment)
        }

        view.userUpdateTxt.setOnClickListener {
            view.userUpdateTxt.visibility = View.GONE
            view.accountnot.visibility = View.GONE
            view.goToBusinessCreate.visibility = View.GONE
            view.photoUrlContainer.visibility = View.VISIBLE
            view.savedUpdatesBtn.visibility = View.VISIBLE
            view.myProfileImage.setOnClickListener {
                launchGalleryIntent()
            }
            view.accountNameEditText.inputType = InputType.TYPE_CLASS_TEXT
            view.accountNameEditText.isClickable = true
            view.accountPhoneEditText.inputType = InputType.TYPE_CLASS_TEXT
            view.accountPhoneEditText.isClickable = true
            view.accountMailEditText.inputType = InputType.TYPE_CLASS_TEXT
            view.accountMailEditText.isClickable = true
            val userName = view.accountNameEditText.text.toString().trim()
            val userPhone = view.accountPhoneEditText.text.toString().trim()
            val userMail = view.accountMailEditText.text.toString().trim()
            val imageUrl = view.photoUrlEditText.text.toString().trim()

            view.savedUpdatesBtn.setOnClickListener {
                updateUser(userName,userMail,userPhone,1,imageUrl,authHeader,id)
            }
        }

        return view
    }

    private fun getDataFromServer(id: Int, token: String) {
        compositeDisposable = CompositeDisposable()
        val retrofit = RetrofitService(Constant.BASE_URL).retrofit.create(InfoAPI::class.java)
        compositeDisposable.add(retrofit.getUserInfo(token, id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResponse,
                { throwable -> println("MyTests: $throwable") }
            ))
    }

    private fun handleResponse(response: UserInfoModel) {

        //  Picasso.get().load(response.photo.toString()).into(myProfileImage)
        val scroll = view.findViewById<ScrollView>(R.id.scrollUserAccount)
        scroll.visibility = View.VISIBLE
        val lottie = view.findViewById<LottieAnimationView>(R.id.loadingUserAccount)
        lottie.visibility = View.GONE
        lottie.pauseAnimation()
        val userFullName = response.fullName
        val userPhoneNumber = response.mobileNumber
        val userEmail = response.email

        println("userfullname:" + response.fullName + response.mobileNumber + response.email)

        view.accountNameEditText.setText(userFullName)
        view.accountPhoneEditText.setText(userPhoneNumber)
        view.accountMailEditText.setText(userEmail)

        if (response.photo == null) {
            view.myProfileImage.setImageResource(R.drawable.setpp)
        } else {
            Picasso.get().load(response.photo).into(view.myProfileImage)
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
                        println(throwable)
                    })
        )
    }
    private fun handleResponseUpdate(response: UpdateResponse) {
        println("Response: " + response.isSuccess)
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
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
                view.photoUrlEditText.setText(imagePath)
                view.myProfileImage.setImageBitmap(compressedBitmap)
                if(compressedBitmap!=null){
                    val compressedImagePath = saveCompressedBitmapToFile(compressedBitmap)
                    view.photoUrlEditText.setText(compressedImagePath)
                    println("CompressedImagePath"+compressedImagePath)
                }
                println(imagePath)
            }
        }
    }
    private fun saveCompressedBitmapToFile(bitmap: Bitmap): String? {
        val outputDir = requireContext().cacheDir // Get the directory to store the compressed image
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
        val cursor = requireActivity().contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                path = it.getString(columnIndex)
            }
        }
        return path
    }
}