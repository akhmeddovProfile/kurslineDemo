package com.example.kurslinemobileapp.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.kurslinemobileapp.R
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        pdfAboutView.fromAsset("about.pdf").load()
    }
}