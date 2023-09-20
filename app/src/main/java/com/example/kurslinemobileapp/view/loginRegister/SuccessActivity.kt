package com.example.kurslinemobileapp.view.loginRegister

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.SpannableString
import android.text.style.UnderlineSpan
import androidx.appcompat.app.AppCompatActivity
import com.app.kurslinemobileapp.R
import com.app.kurslinemobileapp.databinding.ActivitySuccessBinding
import com.example.kurslinemobileapp.view.MainActivity

class SuccessActivity : AppCompatActivity() {
    private val splashDuration = 5000L
    private lateinit var bindingSuccessBinding: ActivitySuccessBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingSuccessBinding=ActivitySuccessBinding.inflate(layoutInflater)
        val view=bindingSuccessBinding.root
        setContentView(view)
        setContentView(R.layout.activity_success)



        val mString = getString(R.string.backToHome)

        // Creating a Spannable String
        // from the above string
        val mSpannableString = SpannableString(mString)

        // Setting underline style from
        // position 0 till length of
        // the spannable string
        mSpannableString.setSpan(UnderlineSpan(), 0, mSpannableString.length, 0)

        // Displaying this spannable
        // string in TextView
        bindingSuccessBinding.backToHomeTv.text = mSpannableString


        Handler().postDelayed({
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
            finish() // Close the splash screen activity
        }, splashDuration)

        bindingSuccessBinding.backToHomeTv.setOnClickListener {
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
            finish() // Close the splash screen activity
        }
    }
}