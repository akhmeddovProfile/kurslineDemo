package com.example.kurslinemobileapp.view.payment

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kurslinemobileapp.R
import kotlinx.android.synthetic.main.activity_enter_card_number_page.*

class EnterCardNumberPage : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_card_number_page)

        print("Value: "+intent.getStringExtra("selectedText"))
        priceSum.text="${intent.getStringExtra("selectedText")} AZN"
    }
}