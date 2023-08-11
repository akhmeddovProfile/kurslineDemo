package com.example.kurslinemobileapp.view.accountsFragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.airbnb.lottie.LottieAnimationView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.adapter.*
import com.example.kurslinemobileapp.api.companyData.CompanyDatasAPI
import com.example.kurslinemobileapp.api.companyData.CompanyRegisterData
import com.example.kurslinemobileapp.api.getUserCmpDatas.InfoAPI
import com.example.kurslinemobileapp.api.getUserCmpDatas.UserCmpInfoModel.UserInfoModel
import com.example.kurslinemobileapp.api.register.RegisterAPI
import com.example.kurslinemobileapp.api.register.RegisterCompanyResponse
import com.example.kurslinemobileapp.api.updateUserCompany.UpdateAPI
import com.example.kurslinemobileapp.api.updateUserCompany.UpdateResponse
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.service.Room.AppDatabase
import com.example.kurslinemobileapp.service.Room.category.MyRepositoryForCategory
import com.example.kurslinemobileapp.view.loginRegister.LoginActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_company_update.*
import kotlinx.android.synthetic.main.activity_register_company.*
import kotlinx.android.synthetic.main.activity_update_user.*
import kotlinx.android.synthetic.main.fragment_business_account.view.*
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
import java.util.regex.Pattern

class CompanyUpdateActivity : AppCompatActivity() {
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var regionAdapter: RegionAdapter
    private lateinit var modeAdapter: ModeAdapter
    private lateinit var statusAdapter: StatusAdapter

    val MAX_IMAGE_WIDTH = 800 // Maximum width for the compressed image
    val MAX_IMAGE_HEIGHT = 600 // Maximum height for the compressed image
    private var block: Boolean = true
    lateinit var categoryId: String
    lateinit var statusId: String
    lateinit var regionId:String

    private var job: Job? = null
    private lateinit var repository: MyRepositoryForCategory

    private var isCategoryChanged: Boolean = false
    private var isStatusChanged: Boolean = false
    private var isRegionChanged: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_update)

         sharedPreferences = this.getSharedPreferences(Constant.sharedkeyname, Context.MODE_PRIVATE)
        val id = sharedPreferences.getInt("userID", 0)
        val token = sharedPreferences.getString("USERTOKENNN", "")
        val authHeader = "Bearer $token"
        println("userID" + id)
        println("userToken" + authHeader)

        repository = MyRepositoryForCategory(
            AppDatabase.getDatabase(this).categoryDao(),
            AppDatabase.getDatabase(this).subCategoryDao()
        )

        businessAccountUpdateCategoryEditText.setOnClickListener {
            showBottomSheetDialog()
        }
        businessAccountUpdateRegionEditText.setOnClickListener {
            showBottomSheetDialogRegions()
        }
        companyUpdateStatusEditText.setOnClickListener {
            showBottomSheetDialogStatus()
        }

        categoryId = ""
        statusId = ""
        regionId = ""

        val categoryCMPid = sharedPreferences.getString("companyCategoryId", "")?:""
        val statusCMPid = sharedPreferences.getString("userStatusId","")?:""
        val regionCMPid = sharedPreferences.getString("companyRegionId","")?:""
        val userFullName = sharedPreferences.getString("companyOwnerName","")?:""
        val userEmail = sharedPreferences.getString("companyEmail","")?:""
        val userPhoneNumber = sharedPreferences.getString("companyNumber","")?:""
        val companyName = sharedPreferences.getString("companyName","")?:""
        val userAddress = sharedPreferences.getString("companyAddress","")?:""
        val userAbout = sharedPreferences.getString("companyAbout","")?:""
        val userPhoto = sharedPreferences.getString("companyPhoto","")?:""
        val companyCategory = sharedPreferences.getString("companyCategory","")?:""
        val companyStatus = sharedPreferences.getString("companyStatus","")?:""
        val companyRegion = sharedPreferences.getString("companyRegion","")?:""
        businessAccountUpdateNameEditText.setText(userFullName)
        businessAccountUpdateEmailEditText.setText(userEmail)
        businessAccountUpdateCompanyEditText.setText(companyName)
        companyUpdateAdressEditText.setText(userAddress)
        businessAccountUpdatePhoneEditText.setText(userPhoneNumber)
        businessAccountAboutEditText.setText(userAbout)
        businessAccountUpdateCategoryEditText.setText(companyCategory)
        companyUpdateStatusEditText.setText(companyStatus)
        businessAccountUpdateRegionEditText.setText(companyRegion)
        Picasso.get().load(userPhoto).into(myCompanyUpdateProfilePhoto)

        savedUpdatesBtnCompany.setOnClickListener {
            val companyNameContainer = businessAccountUpdateNameEditText.text.toString().trim()
            val companyEmailContainer = businessAccountUpdateEmailEditText.text.toString().trim()
            val companyFullNameContainer = businessAccountUpdateCompanyEditText.text.toString().trim()
            val companyAddressContainer = companyUpdateAdressEditText.text.toString().trim()
            val companyPhoneContainer = businessAccountUpdatePhoneEditText.text.toString().trim()
            //  val companyModeContainer = companyModeEditText.text.toString().trim()

            val aboutCompanyContainer = businessAccountAboutEditText.text.toString().trim()
            if (companyNameContainer.isEmpty()) {
                companyNameEditText.error = " Name required"
                companyNameEditText.requestFocus()
                block = false
            }
            if (companyEmailContainer.isEmpty()) {
                companyEmailEditText.error = "Email required"
                companyEmailEditText.requestFocus()
                block = false
            }
            if (companyFullNameContainer.isEmpty()) {
                companyFullNameEditText.error = "Company Name required"
                companyFullNameEditText.requestFocus()
                block = false
            }

            if (companyAddressContainer.isEmpty()) {
                companyAdressEditText.error = "Address required"
                companyAdressEditText.requestFocus()
                block = false
            }

            if (companyPhoneContainer.isEmpty()) {
                companyPhoneEditText.error = "Phone required"
                companyPhoneEditText.requestFocus()
                block = false
            }

            if (aboutCompanyContainer.isEmpty()) {
                aboutCompanyEditText.error = "About Company required"
                aboutCompanyEditText.requestFocus()
                block = false
            }

            val imageUrl = if (companyUpdatePhotoURLEditText.text.toString().isNotEmpty() )
            {
                companyUpdatePhotoURLEditText.text.toString().trim()
            } else {
                null
            }

            val categoryContainer = if (isCategoryChanged) {
                categoryId
            } else {
                categoryCMPid
            }

            val statusContainer = if(isStatusChanged){
                statusId
            }else{
                statusCMPid
            }

            val regionContainer = if (isRegionChanged) {
                regionId
            } else {
                regionCMPid
            }


            showProgressButton(true)
                sendCompanydata(companyNameContainer,companyEmailContainer,companyPhoneContainer,companyFullNameContainer , companyAddressContainer,aboutCompanyContainer,imageUrl,categoryContainer, statusContainer,regionContainer,authHeader,id)
        }
        myCompanyUpdateProfilePhoto.setOnClickListener {
            launchGalleryIntent()
        }
    }

    fun sendCompanydata(
        userFullName: String,
        email: String,
        mobileNumber: String,
        companyName: String,
        companyAddress: String,
        companyAbout: String,
        imagePath: String?,
        companyCategoryId: String,
        companyStatusId:String,
        companyRegionId:String,
        token: String,
        userId: Int
    ) {
        val photo: MultipartBody.Part? = if (imagePath != null) {
            val file = File(imagePath)
            val reqFile: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            MultipartBody.Part.createFormData("Photos", file.name, reqFile)
        } else {
            null
        }
        val companyUsername: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), userFullName)
        val companyemail: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), email)
        val companyNumber: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), mobileNumber)
        val address: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), companyAddress)
        val name: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), companyName)
        val about: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), companyAbout)
        val categoryid: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), companyCategoryId)
        val regionId: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), companyRegionId)
        val statusId: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), companyStatusId)


        compositeDisposable = CompositeDisposable()
        val retrofit =
            RetrofitService(Constant.BASE_URL).retrofit.create(UpdateAPI::class.java)

        compositeDisposable.add(
            retrofit.companyUpdateMethod(companyUsername,companyemail,companyNumber,name,address,about,photo,statusId,categoryid,regionId,token,userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse,
                    { throwable ->
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

    private fun handleResponse(response: UpdateResponse) {
        println("Response: " + response.isSuccess)
        Toast.makeText(this,getString(R.string.saveSuccesfull),Toast.LENGTH_SHORT).show()
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
                companyUpdatePhotoURLEditText.setText(imagePath)
                myCompanyUpdateProfilePhoto.setImageBitmap(compressedBitmap)
                if(compressedBitmap!=null){
                    val compressedImagePath = saveCompressedBitmapToFile(compressedBitmap)
                    companyUpdatePhotoURLEditText.setText(compressedImagePath)
                    println("CompressedImagePath: "+compressedImagePath)
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

        job = repository.getAllCategories().onEach { categories ->
            println("1")
            categoryAdapter = CategoryAdapter(categories)
            recyclerViewCategories.adapter = categoryAdapter
            categoryAdapter.setChanged(categories)
            categoryAdapter.setOnItemClickListener { category ->
                categoryId = category.category.categoryId.toString()
                businessAccountUpdateCategoryEditText.setText(category.category.categoryName)
                isCategoryChanged = true
                dialog.dismiss()
            }
        }.catch {

        }.launchIn(lifecycleScope)

        dialog.show()
    }

   @SuppressLint("MissingInflatedId")
    private fun showBottomSheetDialogRegions() {
       val appDatabase = AppDatabase.getDatabase(applicationContext)
       val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_dialog_region, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(bottomSheetView)
        val recyclerviewRegions: RecyclerView =
            bottomSheetView.findViewById(R.id.recyclerViewRegions)
        recyclerviewRegions.setHasFixedSize(true)
        recyclerviewRegions.setLayoutManager(LinearLayoutManager(this))

       job=appDatabase.regionDao().getAllRegions()
           .onEach { reg->
               println("2")
               regionAdapter = RegionAdapter(reg)
               recyclerviewRegions.adapter = regionAdapter
               regionAdapter.setChanged(reg)
               regionAdapter.setOnItemClickListener { region ->
                   businessAccountUpdateRegionEditText.setText(region.regionName)
                   regionId = region.regionId.toString()
                   isRegionChanged = false
                   dialog.dismiss()
               }
           }.catch {throwable->
               println(throwable)
           }.launchIn(lifecycleScope)

    }
    private fun cancelJob() {
        job?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelJob()
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

        job=appDatabase.statusDao().getAllMode().onEach { status ->
            println("4")
            statusAdapter = StatusAdapter(status)
            recyclerViewStatus.adapter = statusAdapter
            statusAdapter.setChanged(status)
            statusAdapter.setOnItemClickListener { status ->
                companyUpdateStatusEditText.setText(status.statusName)
                statusId = status.statusId.toString()
                isStatusChanged = true
                dialog.dismiss()
            }
        }.catch { throwable ->
            println("MyTestStatus: $throwable")
        }.launchIn(lifecycleScope)
        dialog.show()
    }

    private fun showProgressButton(show: Boolean) {
        if (show) {
            savedUpdatesBtnCompany.apply {
                isEnabled = false
                text = getString(R.string.savingChange)  // Set empty text or loading indicator text
                // Add loading indicator drawable or ProgressBar if needed
            }
        } else {
            savedUpdatesBtnCompany.apply {
                isEnabled = true
                text = getString(R.string.saveChange)
                // Restore original background, text color, etc., if modified
            }
        }
    }
}