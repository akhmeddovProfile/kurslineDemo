package com.example.kurslinemobileapp.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.kurslinemobileapp.databinding.ActivityPdfPrivacyPolicyBinding

class PdfPrivacyPolicy : AppCompatActivity() {
    private lateinit var bindingPdfPrivacyPolicyBinding: ActivityPdfPrivacyPolicyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingPdfPrivacyPolicyBinding=ActivityPdfPrivacyPolicyBinding.inflate(layoutInflater)
        val view=bindingPdfPrivacyPolicyBinding.root
        setContentView(view)
        //setContentView(R.layout.activity_pdf_privacy_policy)
        bindingPdfPrivacyPolicyBinding.pdfPrivacyView.fromAsset("mexfilikword.pdf").load()
    }
}