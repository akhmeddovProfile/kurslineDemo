package com.example.kurslinemobileapp.view.loginRegister

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.adapter.CategoryAdapter
import com.example.kurslinemobileapp.adapter.ModeAdapter
import com.example.kurslinemobileapp.adapter.RegionAdapter
import com.example.kurslinemobileapp.adapter.StatusAdapter
import com.example.kurslinemobileapp.api.companyData.CompanyDatasAPI
import com.example.kurslinemobileapp.api.register.RegisterAPI
import com.example.kurslinemobileapp.api.register.RegisterCompanyResponse
import com.example.kurslinemobileapp.api.register.UserToCompanyResponse
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.service.Room.AppDatabase
import com.example.kurslinemobileapp.service.Room.category.MyRepositoryForCategory
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_register_company.*
import kotlinx.android.synthetic.main.activity_user_register.*
import kotlinx.android.synthetic.main.activity_user_to_company.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class UserToCompanyActivity : AppCompatActivity() {
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var statusAdapter: StatusAdapter
    var compositeDisposable = CompositeDisposable()

    //Gallery
    val MAX_IMAGE_WIDTH = 800 // Maximum width for the compressed image
    val MAX_IMAGE_HEIGHT = 600 // Maximum height for the compressed image
    private var reenterPermissionRequestCode = 0

    private var block: Boolean = true
    lateinit var editor: SharedPreferences.Editor
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var categoryId: String
    lateinit var statusId: String

    private var job: Job? = null
    private lateinit var repository: MyRepositoryForCategory


    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_to_company)
        repository = MyRepositoryForCategory(
            AppDatabase.getDatabase(this).categoryDao(),
            AppDatabase.getDatabase(this).subCategoryDao()
        )
        sharedPreferences = getSharedPreferences(Constant.sharedkeyname, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        val userId = sharedPreferences.getInt("userID",0)
        val token = sharedPreferences.getString("USERTOKENNN","")
        val authHeader = "Bearer $token"
        println("userid" + userId)
        println("token:"+authHeader)

        userToCompanyNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used
            }

            override fun afterTextChanged(s: Editable?) {
                val name = s.toString().trim()
                val characterCount = name.length

                if (characterCount < 3 || characterCount > 50) {
                    characterCountTextViewuserToCompanyName.visibility=View.VISIBLE
                    userToCompanyNameContainer.error = getString(R.string.busienssNameCharacterCount)
                } else {
                    characterCountTextViewuserToCompanyName.visibility=View.GONE
                    userToCompanyNameContainer.error = null
                }

                characterCountTextViewuserToCompanyName.text = "$characterCount / 50"
            }
        })

        userToCompanyAddressEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used
            }

            override fun afterTextChanged(s: Editable?) {
                val name = s.toString().trim()
                val characterCount = name.length

                if (characterCount < 3 || characterCount > 200) {
                    userToCompanyAddressContainer.error = getString(R.string.addressCharacterCount)
                } else {
                    userToCompanyAddressContainer.error = null
                }

                characterCountTextViewuserToCompanyAddress.text = "$characterCount / 200"
            }
        })

        userToCompanyAboutEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used
            }

            override fun afterTextChanged(s: Editable?) {
                val name = s.toString().trim()
                val characterCount = name.length

                if (characterCount < 3 || characterCount > 1500) {
                    userToCompanyAboutContainer.error = getString(R.string.aboutCharacterCount)
                } else {
                    userToCompanyAboutContainer.error = null
                }

                characterCountTextViewuserToCompanyAbout.text = "$characterCount / 1500"
            }
        })

        userToCompanyCategoryEditText.setOnClickListener {
            showBottomSheetDialog()
        }

        userToCompanyStatusEditText.setOnClickListener {
            showBottomSheetDialogStatus()
        }
        categoryId = ""
        statusId = ""


        userToCompanyRegisterBtn.setOnClickListener{
            block = true
            val companyNameContainer = userToCompanyNameEditText.text.toString().trim()
            val companyAddressContainer = userToCompanyAddressEditText.text.toString().trim()
            val photoContainer = userToCompanyPhotoEditText.text.toString().trim()
            val companyStatusContainer = statusId
            val companyCategoryContainer = categoryId
            val aboutCompanyContainer = userToCompanyAboutEditText.text.toString().trim()
            showProgressButton(true)
            sendCompanydata(companyNameContainer, companyCategoryContainer, companyAddressContainer, aboutCompanyContainer, photoContainer, companyStatusContainer, authHeader, userId)
        }
        userToCompanyPhotoEditText.setOnClickListener {
            launchGalleryIntent()
        }
    }

    fun sendCompanydata(
        companyName: String,
        companyCategoryId: String,
        companyAddress: String,
        companyAbout: String,
        imagePath: String,
        companyStatusId:String,
        token:String,userId:Int
    ) {
        val file = File(imagePath)
        val reqFile: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val photo: MultipartBody.Part =
            MultipartBody.Part.createFormData("photo", file.name, reqFile)
        val companyame: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), companyName)
        val address: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), companyAddress)
        val about: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), companyAbout)
        val categoryid: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), companyCategoryId)
        val statusId: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), companyStatusId)


        compositeDisposable = CompositeDisposable()
        val retrofit =
            RetrofitService(Constant.BASE_URL).retrofit.create(RegisterAPI::class.java)

        compositeDisposable.add(
            retrofit.userToCompanyCreate(companyame,categoryid,address,about,photo,statusId,token,userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse,
                    { throwable ->
                        println(throwable.message)
                        if (throwable.message!!.contains("HTTP 409")){
                            Toast.makeText(this,getString(R.string.http409String),Toast.LENGTH_SHORT).show()
                        }else{
                            val text = getString(R.string.infosWrong)
                            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
                        }
                        showProgressButton(false)
                    })
        )
    }

    private fun handleResponse(response: UserToCompanyResponse) {
        println("Response: " + response.isSuccess)
        Toast.makeText(this,getString(R.string.registerSuccess),Toast.LENGTH_SHORT).show()
        val intent = Intent(this@UserToCompanyActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun launchGalleryIntent() {
       val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, Constant.PICK_IMAGE_REQUEST)
 /*       if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission already granted, launch the gallery intent
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, Constant.PICK_IMAGE_REQUEST)
        } else {
            // Request permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                Constant.PERMISSION_READ_EXTERNAL_STORAGE
            )
        }*/
        /*        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
             val uri = Uri.fromParts("package", packageName, null)
             intent.data = uri*/
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
    private fun showPermissionRequestDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("The app needs access to your gallery to select images. Would you like to grant permission now?")
            .setPositiveButton("Yes") { dialog, which ->
                // Request permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constant.PERMISSION_READ_EXTERNAL_STORAGE
                )
            }
            .setNegativeButton("No") { dialog, which ->
                // User chose not to grant permission, you can show a message or take other actions
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data?.data
            val imagePath = selectedImageUri?.let { getRealPathFromURI(it) }
            if (imagePath != null) {
                val compressedBitmap = compressImageFile(imagePath)
                userToCompanyPhotoEditText.setText(imagePath)
                if(compressedBitmap!=null){
                    val compressedImagePath = saveCompressedBitmapToFile(compressedBitmap)
                    userToCompanyPhotoEditText.setText(compressedImagePath)
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
    @SuppressLint("MissingInflatedId", "NotifyDataSetChanged")

    private fun showBottomSheetDialog() {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(bottomSheetView)
        val recyclerViewCategories: RecyclerView =
            bottomSheetView.findViewById(R.id.recyclerViewCategories)
        recyclerViewCategories.setHasFixedSize(true)
        recyclerViewCategories.setLayoutManager(LinearLayoutManager(this))

        job=repository.getAllCategories().onEach { categories ->
            println("1")
            categoryAdapter = CategoryAdapter(categories)
            recyclerViewCategories.adapter = categoryAdapter
            categoryAdapter.setChanged(categories)
            categoryAdapter.setOnItemClickListener { category ->
                categoryId = category.category.categoryId.toString()
                userToCompanyCategoryEditText.setText(category.category.categoryName)
                dialog.dismiss()
            }
        }.catch {

        }.launchIn(lifecycleScope)

        dialog.show()
    }



    @SuppressLint("MissingInflatedId")
    private fun showBottomSheetDialogStatus() {
        val appDatabase = AppDatabase.getDatabase(applicationContext)
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_dialog_status, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(bottomSheetView)
        val recyclerViewStatus: RecyclerView =
            bottomSheetView.findViewById(R.id.recyclerViewStatus)
        recyclerViewStatus.setHasFixedSize(true)
        recyclerViewStatus.setLayoutManager(LinearLayoutManager(this))
        job=appDatabase.statusDao().getAllMode()
            .onEach { status ->
                println("4")
                statusAdapter = StatusAdapter(status)
                recyclerViewStatus.adapter = statusAdapter
                statusAdapter.setChanged(status)
                statusAdapter.setOnItemClickListener { status ->
                    statusId = status.statusId.toString()
                    userToCompanyStatusEditText.setText(status.statusName)
                    dialog.dismiss()
                }
            }.catch { throwable ->
                println("MyTestStatus: $throwable")
            }.launchIn(lifecycleScope)

        dialog.show()
    }

    private fun showProgressButton(show: Boolean) {
        if (show) {
            userToCompanyRegisterBtn.apply {
                isEnabled = false
                text = getString(R.string.registerContinue)  // Set empty text or loading indicator text
                // Add loading indicator drawable or ProgressBar if needed
            }
        } else {
            userToCompanyRegisterBtn.apply {
                isEnabled = true
                text = getString(R.string.createBusiness)
                // Restore original background, text color, etc., if modified
            }
        }
    }
}

