package com.example.kurslinemobileapp.view.loginRegister

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kurslinemobileapp.R
import kotlinx.android.synthetic.main.activity_main_register.*

class MainRegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_register)

        goToUserRegister.setOnClickListener {
            val intent = Intent(this@MainRegisterActivity, UserRegisterActivity::class.java)
            startActivity(intent)
        }

        goToBusinessRegister.setOnClickListener {
           val intent = Intent(this@MainRegisterActivity, RegisterCompanyActivity::class.java)
            startActivity(intent)
        }
    }
}