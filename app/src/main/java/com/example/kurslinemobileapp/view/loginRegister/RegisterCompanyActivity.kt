package com.example.kurslinemobileapp.view.loginRegister

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.adapter.CategoryAdapter
import com.example.kurslinemobileapp.adapter.RegionAdapter
import com.example.kurslinemobileapp.api.companyData.CompanyDatasAPI
import com.example.kurslinemobileapp.api.register.RegisterAPI
import com.example.kurslinemobileapp.api.register.RegisterCompanyResponse
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.Constant.PICK_IMAGE_REQUEST
import com.example.kurslinemobileapp.service.Constant.sharedkeyname
import com.example.kurslinemobileapp.service.RetrofitService
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_register_company.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream

class RegisterCompanyActivity : AppCompatActivity() {
    private lateinit var categoryApi: CompanyDatasAPI
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var regionAdapter: RegionAdapter
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
    val REQUEST_EXTERNAL_STORAGE = 100
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


        companyCategoryEditText.setOnClickListener {
            showBottomSheetDialog()
        }
        companyRegionEditText.setOnClickListener {
            showBottomSheetDialogRegions()
        }
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




            //burada galeriyadan seklin url nece goture bilerem?
            //1.hemen url bunun yerine isletmeliyem -> companyPhoto.text.toString()
            //2.galeriyadan sekli 258 setrde gotururem
            sendCompanydata("2",aboutCompanyContainer,companyAddressContainer,companyNameContainer,"1",companyPasswordContainer,
                companyPhoneContainer,companyEmailContainer,companyFullNameContainer,companyPhoto.text.toString())

        }
        /*downloadPhotoFromGalery()*/

        companyPhoto.setOnClickListener {
            /*selectCertificate(it)*/
            launchGalleryIntent()

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


        compositeDisposable = CompositeDisposable()
        val retrofit =
            RetrofitService(Constant.BASE_URL).retrofit.create(RegisterAPI::class.java)

        compositeDisposable.add(
            retrofit.createCompany(username,companyemail,companyNumber,companyPassword,companyGender,name,address,about,categoryid,photos)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse,
                    { throwable ->
                        println(throwable) })
        )

    }

    private fun handleResponse(response: RegisterCompanyResponse) {
        println("Response: " + response.isSuccess)
        val intent = Intent(this@RegisterCompanyActivity, LoginActivity::class.java)
        startActivity(intent)
    }



/*    @SuppressLint("NewApi")
    fun getPathFromURI(uri: Uri?): String? {
        var filePath = ""
        val wholeID = DocumentsContract.getDocumentId(uri)

        // Split at colon, use second item in the array
        val id = wholeID.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
        val column = arrayOf(MediaStore.Images.Media.DATA)

        // where id is equal to
        val sel = MediaStore.Images.Media._ID + "=?"
        val cursor: Cursor? = this.getContentResolver().query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            column, sel, arrayOf<String>(id), null
        )
        val columnIndex = cursor?.getColumnIndex(column[0])
        if (cursor!!.moveToFirst()) {
            filePath = cursor.getString(columnIndex!!)
        }
        cursor.close()
        return filePath
    }*/

    fun launchGalleryIntent() {

        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data?.data
            val imagePath = selectedImageUri?.let { getRealPathFromURI(it) }
            if (imagePath != null) {
                companyPhoto.setText(imagePath)
                println(imagePath)
            }
        }
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

/*    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_EXTERNAL_STORAGE && resultCode == RESULT_OK) {
            var path: String? = null
            val clipData = data!!.clipData
            if (clipData != null) {
                for (i in 0 until clipData.itemCount) {
                    val imageUri = clipData.getItemAt(i).uri
                    Log.d("URI", imageUri.path!!)
                    try {
                        val inputStream: InputStream? =
                            this.getContentResolver().openInputStream(imageUri)
                        // Static.sellectImageIsChange = true;

                        path =getPathFromURI(imageUri)

                        Log.d("TAG", "File Path: $path")
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
                }
            } else {
                val imageUri = data.data
                try {
                    val inputStream: InputStream? =
                        this.getContentResolver().openInputStream(imageUri!!)
                    path = getPathFromURI(imageUri)
                    companyPhoto.setText(path!!)
                    //                    Static.sellectImageIsChange = true;
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }*/

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
    @SuppressLint("MissingInflatedId", "NotifyDataSetChanged")
    private fun showBottomSheetDialog() {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(bottomSheetView)
        val recyclerViewCategories: RecyclerView = bottomSheetView.findViewById(R.id.recyclerViewCategories)
        recyclerViewCategories.setHasFixedSize(true)
        recyclerViewCategories.setLayoutManager(LinearLayoutManager(this))
        compositeDisposable = CompositeDisposable()
        val retrofit = RetrofitService(Constant.BASE_URL).retrofit.create(CompanyDatasAPI::class.java)
        compositeDisposable.add(retrofit.getCategories()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ categories ->
                println("1")
                categoryAdapter = CategoryAdapter(categories.categories)
                recyclerViewCategories.adapter = categoryAdapter
                regionAdapter.setChanged(categories.regions)
                println("2")
            }, { throwable-> println("MyTests: $throwable") }))

        dialog.show()
    }

    @SuppressLint("MissingInflatedId")
    private fun showBottomSheetDialogRegions() {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_dialog_region, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(bottomSheetView)
        val recyclerviewRegions: RecyclerView = bottomSheetView.findViewById(R.id.recyclerViewRegions)
        recyclerviewRegions.setHasFixedSize(true)
        recyclerviewRegions.setLayoutManager(LinearLayoutManager(this))
        compositeDisposable = CompositeDisposable()
        val retrofit = RetrofitService(Constant.BASE_URL).retrofit.create(CompanyDatasAPI::class.java)
        compositeDisposable.add(retrofit.getRegions()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ reg ->
                println("3")
                regionAdapter = RegionAdapter(reg.regions)
                recyclerviewRegions.adapter = regionAdapter
                regionAdapter.setChanged(reg.regions)
                println("4")
            }, { throwable-> println("MyTestsRegions: $throwable") }))
        dialog.show()
    }


}