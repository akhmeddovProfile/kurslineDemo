package com.example.kurslinemobileapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kurslinemobileapp.R
import kotlinx.android.synthetic.main.activity_pdf_privacy_policy.*

class PdfPrivacyPolicy : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_privacy_policy)
        pdfPrivacyView.fromAsset("mexfilikword.pdf").load()
    }


}