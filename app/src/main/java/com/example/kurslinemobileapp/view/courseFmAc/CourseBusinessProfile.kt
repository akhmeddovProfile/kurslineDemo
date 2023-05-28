package com.example.kurslinemobileapp.view.courseFmAc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.adapter.MainListProductAdapter
import com.example.kurslinemobileapp.model.mainpage.Product

class CourseBusinessProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_business_profile)
        getProducts()
    }

    private fun getProducts() {
        val productInformation = listOf(
            Product(
                "Online",
                R.drawable.vip_icon,
                R.drawable.yenielan,
                "Mobile Programming",
                "Aim Tech",
                "This course for test"
            ),
            Product(
                "Online",
                R.drawable.vip_icon,
                R.drawable.yenielan,
                "Mobile Programming",
                "Aim Tech",
                "This course for test"
            )
        )
        val recyclerviewForProducts = findViewById<RecyclerView>(R.id.courseBusinessUploadsRV)
        val adapter = MainListProductAdapter(productInformation)
        recyclerviewForProducts.adapter = adapter
        recyclerviewForProducts.layoutManager = GridLayoutManager(this,2)
    }
}