package com.example.kurslinemobileapp.view.loginRegister

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.adapter.CategoryAdapter
import com.example.kurslinemobileapp.adapter.ModeAdapter
import com.example.kurslinemobileapp.adapter.RegionAdapter
import com.example.kurslinemobileapp.adapter.StatusAdapter
import com.example.kurslinemobileapp.api.register.RegisterAPI
import com.example.kurslinemobileapp.api.register.RegisterCompanyResponse
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.Constant.sharedkeyname
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.service.Room.AppDatabase
import com.example.kurslinemobileapp.service.Room.category.MyRepositoryForCategory
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_register_company.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.*
import java.util.regex.Pattern


class RegisterCompanyActivity : AppCompatActivity() {
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var regionAdapter: RegionAdapter
    private lateinit var modeAdapter: ModeAdapter
    private lateinit var statusAdapter: StatusAdapter
    var compositeDisposable = CompositeDisposable()

    private lateinit var repository: MyRepositoryForCategory
    private var job: Job? = null

    val MAX_IMAGE_WIDTH = 800 // Maximum width for the compressed image
    val MAX_IMAGE_HEIGHT = 600 // Maximum height for the compressed image

    companion object {
        private const val PICK_IMAGE_REQUEST = 1 // You can use any integer value
        private const val REQUEST_CODE_PERMISSIONS = 2 // Another unique integer value
    }

    private fun checkPermission(): Boolean {
        return if (SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val result = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE)
            val result1 = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)
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
    //local data save
    private var block: Boolean = true
    lateinit var editor: SharedPreferences.Editor
    private lateinit var sharedPreferences: SharedPreferences

    //Variable
    lateinit var name: String
    lateinit var companyEmail: String
    lateinit var companyPassword: String
    lateinit var companyFullName: String
    lateinit var companyRegion: String
    lateinit var companyAddress: String
    lateinit var companyPhone: String
    lateinit var companyMode: String
    lateinit var companyStatus: String
    lateinit var companyCategory: String
    lateinit var aboutCompany: String
    lateinit var categoryId: String
    lateinit var statusId: String
    lateinit var regionId:String

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_company)


        sharedPreferences = getSharedPreferences(sharedkeyname, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        repository = MyRepositoryForCategory(
            AppDatabase.getDatabase(this).categoryDao(),
            AppDatabase.getDatabase(this).subCategoryDao()
        )

        companyFullNameEditText.addTextChangedListener(object : TextWatcher {
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
                    companyFullNameContainer.error = getString(R.string.nameCharacterCount)
                } else {
                    companyFullNameContainer.error = null
                }

                characterCountTextViewcmpname.text = "$characterCount / 50"
            }
        })

        CompanypasswordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used
            }

            override fun afterTextChanged(s: Editable?) {
                val password = s.toString().trim()
                val isValid = isPasswordValid(password)

                if (!isValid) {
                    companyPasswordContainer.error = getString(R.string.passwordCount)
                } else {
                    companyPasswordContainer.error = null
                }
            }
        })

        companyFullNameEditText.addTextChangedListener(object : TextWatcher {
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
                    companyFullNameContainer.error = getString(R.string.busienssNameCharacterCount)
                } else {
                    companyFullNameContainer.error = null
                }

                characterCountTextViewcmpname.text = "$characterCount / 50"
            }
        })


        companyCategoryEditText.setOnClickListener {
            showBottomSheetDialog()
        }
        compantStatusEditText.setOnClickListener {
            showBottomSheetDialogStatus()
        }
        categoryId = ""
        statusId = ""
        regionId = ""
        createBusinessAccountBtn.setOnClickListener {
            block = true
            //val companyNameContainer = companyNameEditText.text.toString().trim()
            val companyEmailContainer = companyEmailEditText.text.toString().trim()
            val companyPasswordContainer = CompanypasswordEditText.text.toString().trim()
            val companyFullNameContainer = companyFullNameEditText.text.toString().trim()
            val companyPhoneContainer = companyPhoneEditText.text.toString().trim()
            //  val companyModeContainer = companyModeEditText.text.toString().trim()
            val companyStatusContainer = statusId
            val companyCategoryContainer = categoryId
            //val companyRegionContainer = regionId
            val aboutCompanyContainer = aboutCompanyEditText.text.toString().trim()
            val companyPhotoContainer = companyPhoto.text.toString().trim()
            showProgressButton(true)
            sendCompanydata(companyEmailContainer,"+994"+companyPhoneContainer,companyPasswordContainer,companyFullNameContainer ,aboutCompanyContainer,companyCategoryContainer,companyPhotoContainer,companyStatusContainer)
        }
        companyPhoto.setOnClickListener {
            if(!checkPermission()){
                checkAndRequestPermissions()
         /*       val miuiVersion = BuildProperties.getMIUIVersion()
                if (miuiVersion == "14.0.3") {
                    showMIUIExplanationDialog() // Show a custom explanation
                }*/
                requestPermissionLauncher.launch(READ_EXTERNAL_STORAGE) // Launch permission request directly
            }
            else{
                openGallery()
            }
           // launchGalleryIntent()
        }
    }


    fun sendCompanydata(
        email: String,
        mobileNumber: String,
        password: String,
        companyName: String,
        companyAbout: String,
        companyCategoryId: String,
        imagePath: String,
        companyStatusId:String) {
        val file = File(imagePath)
        val reqFile: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val photo: MultipartBody.Part =
            MultipartBody.Part.createFormData("photos", file.name, reqFile)
        val companyemail: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), email)
        val companyNumber: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), mobileNumber)
        val companyPassword: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), password)
        val name: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), companyName)
        val about: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), companyAbout)
        val categoryid: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), companyCategoryId)

        val statusId: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), companyStatusId)


        compositeDisposable = CompositeDisposable()
        val retrofit =
            RetrofitService(Constant.BASE_URL).retrofit.create(RegisterAPI::class.java)

        compositeDisposable.add(
            retrofit.createCompany(name,about,companyemail,companyNumber,statusId,categoryid,companyPassword,photo)
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

    private fun handleResponse(response: RegisterCompanyResponse) {
        println("Response: " + response.isSuccess)
        Toast.makeText(this,getString(R.string.registerSuccess),Toast.LENGTH_SHORT).show()
        val intent = Intent(this@RegisterCompanyActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Permission is already granted, proceed with accessing gallery
                openGallery()
            } else {
                // Request permission
                requestPermissionLauncher.launch(READ_MEDIA_IMAGES)
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
                requestPermissionLauncher.launch(READ_EXTERNAL_STORAGE)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
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
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data?.data
            val imagePath = selectedImageUri?.let { getRealPathFromURI(it) }
            if (imagePath != null) {
                val compressedBitmap = compressImageFile(imagePath)
                companyPhoto.setText(imagePath)
                if(compressedBitmap!=null){
                    val compressedImagePath = saveCompressedBitmapToFile(compressedBitmap)
                    companyPhoto.setText(compressedImagePath)
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

    override fun onPause() {
        //getValues()
        super.onPause()
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
        compositeDisposable = CompositeDisposable()

        job=repository.getAllCategories().onEach { categories ->
            println("1")
            categoryAdapter = CategoryAdapter(categories)
            recyclerViewCategories.adapter = categoryAdapter
            categoryAdapter.setChanged(categories)
            categoryAdapter.setOnItemClickListener { category ->
                categoryId = category.category.categoryId.toString()
                companyCategoryEditText.setText(category.category.categoryName)
                dialog.dismiss()
            }
        }.catch { throwable ->
            println("MyTests: $throwable")
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
        compositeDisposable = CompositeDisposable()

        job= appDatabase.regionDao().getAllRegions()
            .onEach {reg->
                println("2")
                regionAdapter = RegionAdapter(reg)
                recyclerviewRegions.adapter = regionAdapter
                regionAdapter.setChanged(reg)
                regionAdapter.setOnItemClickListener { region ->
                 //   companyRegionEditText.setText(region.regionName)
                    regionId = region.regionId.toString()
                    dialog.dismiss()
                }

            }.catch {thorawable->
                println("Error message 2: "+ thorawable.message)
            }.launchIn(lifecycleScope)

        dialog.show()
    }

    private fun cancelJob() {
        job?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelJob()
    }
    @SuppressLint("MissingInflatedId")
    private fun showBottomSheetDialogMode() {
        val appDatabase = AppDatabase.getDatabase(applicationContext)
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_dialog_mode, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(bottomSheetView)
        val recyclerViewMode: RecyclerView =
            bottomSheetView.findViewById(R.id.recyclerViewMode)
        recyclerViewMode.setHasFixedSize(true)
        recyclerViewMode.setLayoutManager(LinearLayoutManager(this))
        compositeDisposable = CompositeDisposable()

        job=appDatabase.modeDao().getAllMode()
            .onEach { mode ->
                println("3")
                modeAdapter = ModeAdapter(mode)
                recyclerViewMode.adapter = modeAdapter
                modeAdapter.setChanged(mode)
                modeAdapter.setOnItemClickListener { mode ->
                   // companyModeEditText.setText(mode.modeName)
                    dialog.dismiss()
                }
            }.catch { throwable ->
                println("MyTestMode: $throwable")
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
                    compantStatusEditText.setText(status.statusName)
                    statusId = status.statusId.toString()
                    dialog.dismiss()
                }
            }.catch { throwable ->
                println("MyTestStatus: $throwable")
            }.launchIn(lifecycleScope)
        dialog.show()
    }

    private fun showProgressButton(show: Boolean) {
        if (show) {
            createBusinessAccountBtn.apply {
                isEnabled = false
                text = getString(R.string.registerContinue)  // Set empty text or loading indicator text
                // Add loading indicator drawable or ProgressBar if needed
            }
        } else {
            createBusinessAccountBtn.apply {
                isEnabled = true
                text = getString(R.string.registerString)
                // Restore original background, text color, etc., if modified
            }
        }
    }
    private fun isPasswordValid(password: String): Boolean {
        val passwordPattern = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$"
        )
        return passwordPattern.matcher(password).matches()
    }
}
