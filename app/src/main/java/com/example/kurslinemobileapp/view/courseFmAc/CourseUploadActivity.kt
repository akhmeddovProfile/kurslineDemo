package com.example.kurslinemobileapp.view.courseFmAc

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.adapter.PhotoPagerAdapter
import com.example.kurslinemobileapp.adapter.RegionAdapter
import com.example.kurslinemobileapp.api.announcement.createAnnouncement.CreataAnnouncementApi
import com.example.kurslinemobileapp.api.announcement.createAnnouncement.CreateAnnouncementRequest
import com.example.kurslinemobileapp.api.companyData.CompanyDatasAPI
import com.example.kurslinemobileapp.model.uploadPhoto.PhotoUpload
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.view.MainActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_all_companies.*
import kotlinx.android.synthetic.main.activity_course_upload.*
import kotlinx.android.synthetic.main.activity_register_company.*
import retrofit2.create


class CourseUploadActivity : AppCompatActivity() {
    private val selectedPhotos = mutableListOf<PhotoUpload>()
    var compositeDisposable = CompositeDisposable()
    private lateinit var regionAdapter: RegionAdapter
    private var block: Boolean = true
    companion object {
        private const val REQUEST_CODE_GALLERY = 1
    }

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_upload)

            backtoMainFromCourseUpload.setOnClickListener {
                val intent = Intent(this@CourseUploadActivity,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        val adapter =
            PhotoPagerAdapter(emptyList()) // Customize the adapter implementation as needed
        viewPagerCourseUpload.adapter = adapter
        uploadCourse.setOnClickListener {
        block=true
            val companyRegionContainer = courseRegionEditText.text.toString().trim()
            val companyCategoryContainer=courseCategoryEditText.text.toString().trim()
            val companyAboutContainer=courseAboutEditText.text.toString().trim()
            val companyTeacherContainer=courseTeacherEditText.text.toString().trim()
            val companyPriceContainer=coursePriceEditText.text.toString().trim()
            val companyModeContainer=courseModeEditText.text.toString().trim()

            if (companyRegionContainer.isEmpty()){
                courseRegionEditText.error="Region required"
                courseRegionEditText.requestFocus()
                block=false
            }
            if (companyCategoryContainer.isEmpty()){
                courseCategoryEditText.error="Category required"
                courseCategoryEditText.requestFocus()
                block=false
            }
            if (companyAboutContainer.isEmpty()){
                courseAboutEditText.error="About the Course required"
                courseAboutEditText.requestFocus()
                block=false
            }
            if (companyTeacherContainer.isEmpty()){
                courseTeacherEditText.error="Teacher required"
                courseTeacherEditText.requestFocus()
                block=false
            }
            if (companyPriceContainer.isEmpty()){
                coursePriceEditText.error="Price required"
                coursePriceEditText.requestFocus()
                block=false
            }
            if (companyModeContainer.isEmpty()){
                courseModeEditText.error="Mode required"
                courseModeEditText.requestFocus()
                block=false
            }



        }


        addCoursePhotos.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(intent, REQUEST_CODE_GALLERY)
            leftArrow.visibility = View.VISIBLE
            rightarrow.visibility = View.VISIBLE
        }

        val viewPager: ViewPager2 = findViewById(R.id.viewPagerCourseUpload)
        leftArrow.setOnClickListener {
            val currentItem = viewPager.currentItem
            if (currentItem > 0) {
                viewPager.setCurrentItem(currentItem - 1, true)
            }
        }

        rightarrow.setOnClickListener {
            val currentItem = viewPager.currentItem
            if (currentItem < selectedPhotos.size - 1) {
                viewPager.setCurrentItem(currentItem + 1, true)
            }
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateNavigationButtons(position)
            }
        })
        courseRegionEditText.setOnClickListener {
            showBottomSheetDialogRegions()
        }

        courseCategoryEditText.setOnClickListener {

        }
    }

    private fun sendAnnouncementData(
        nameofAnnouncement:String,
        nameofCategory:String,
        aboutTheCourse:String,
        nameofTeachers:String,
        priceofCourse:Int,
        modeofCourse:String
    ) {
        compositeDisposable= CompositeDisposable()
        val retrofitService=RetrofitService(Constant.BASE_URL).retrofit.create(CreataAnnouncementApi::class.java)
        //val request=CreateAnnouncementRequest(nameofAnnouncement,nameofCategory,aboutTheCourse,nameofTeachers,priceofCourse,modeofCourse)
    }

    private fun updateNavigationButtons(position: Int) {
        leftArrow.isEnabled = position > 0
        rightarrow.isEnabled = position < selectedPhotos.size - 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK) {
            if (data?.clipData != null) {
                val clipData = data.clipData
                selectedPhotos.clear() // Clear the existing selection
                for (i in 0 until clipData!!.itemCount) {
                    val name =clipData.getItemAt(i).text.toString()
                    val uri = clipData.getItemAt(i).uri
                    selectedPhotos.add(PhotoUpload(name,uri))
                }
            } else if (data?.data != null) {
                val uri = data.data
                val imagePath = uri?.let { getRealPathFromURI(it) }
                selectedPhotos.clear() // Clear the existing selection
                selectedPhotos.add(PhotoUpload(imagePath!!,uri!!))
            }

            val adapter = viewPagerCourseUpload.adapter as? PhotoPagerAdapter
            adapter?.photoList = selectedPhotos
            adapter?.notifyDataSetChanged()
        }

        super.onActivityResult(requestCode, resultCode, data)
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
    @SuppressLint("MissingInflatedId")
    private fun showBottomSheetDialogRegions() {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_dialog_region, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(bottomSheetView)
        val recyclerviewRegions: RecyclerView =
            bottomSheetView.findViewById(R.id.recyclerViewRegions)
        recyclerviewRegions.setHasFixedSize(true)
        recyclerviewRegions.setLayoutManager(LinearLayoutManager(this))
        compositeDisposable = CompositeDisposable()
        val retrofit =
            RetrofitService(Constant.BASE_URL).retrofit.create(CompanyDatasAPI::class.java)
        compositeDisposable.add(
            retrofit.getRegions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ reg ->
                    println("2")
                    regionAdapter = RegionAdapter(reg.regions)
                    recyclerviewRegions.adapter = regionAdapter
                    regionAdapter.setChanged(reg.regions)
                    regionAdapter.setOnItemClickListener { region ->
                        companyRegionEditText.setText(region.regionName)
                        dialog.dismiss()
                    }
                }, { throwable -> println("MyTestsRegions: $throwable") })
        )
        dialog.show()
    }
}