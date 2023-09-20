package com.example.kurslinemobileapp.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.kurslinemobileapp.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    private lateinit var bindinAboutBinding: ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindinAboutBinding=ActivityAboutBinding.inflate(layoutInflater)
        val view=bindinAboutBinding.root
        setContentView(view)
        //setContentView(R.layout.activity_about)

        bindinAboutBinding.pdfAboutView.fromAsset("about.pdf").load()
    }
}