package com.example.kurslinemobileapp.view.loginRegister

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.kurslinemobileapp.databinding.ActivityMainRegisterBinding

class MainRegisterActivity : AppCompatActivity() {
    private lateinit var  bindingMReg: ActivityMainRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingMReg= ActivityMainRegisterBinding.inflate(layoutInflater)
        val view = bindingMReg.root
        setContentView(view)
        //setContentView(R.layout.activity_main_register)

        bindingMReg.goToUserRegister.setOnClickListener {
            val intent = Intent(this@MainRegisterActivity, UserRegisterActivity::class.java)
            startActivity(intent)
        }

        bindingMReg.goToBusinessRegister.setOnClickListener {
            val intent = Intent(this@MainRegisterActivity, RegisterCompanyActivity::class.java)
            startActivity(intent)
        }
    }
}