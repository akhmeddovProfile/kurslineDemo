package com.example.kurslinemobileapp.view.courseFmAc

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.adapter.PhotoPagerAdapter
import com.example.kurslinemobileapp.model.uploadPhoto.Photo
import kotlinx.android.synthetic.main.activity_course_upload.*


class CourseUploadActivity : AppCompatActivity() {
    private val selectedPhotos = mutableListOf<Photo>()
    companion object {
        private const val REQUEST_CODE_GALLERY = 1
    }
    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_upload)

        val adapter = PhotoPagerAdapter(emptyList()) // Customize the adapter implementation as needed
        viewPagerCourseUpload.adapter = adapter

        addCoursePhotos.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(intent, REQUEST_CODE_GALLERY)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK) {
            if (data?.clipData != null) {
                val clipData = data.clipData
                selectedPhotos.clear() // Clear the existing selection
                for (i in 0 until clipData!!.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    selectedPhotos.add(Photo(uri))
                }
            } else if (data?.data != null) {
                val uri = data.data
                selectedPhotos.clear() // Clear the existing selection
                selectedPhotos.add(Photo(uri!!))
            }

            val adapter = viewPagerCourseUpload.adapter as? PhotoPagerAdapter
            adapter?.photoList = selectedPhotos
            adapter?.notifyDataSetChanged()
        }

        super.onActivityResult(requestCode, resultCode, data)
    }


}