package com.example.kurslinemobileapp.view.payment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.view.courseFmAc.ProductDetailActivity
import kotlinx.android.synthetic.main.activity_move_forward_ann.*
import kotlinx.android.synthetic.main.activity_vip_payment_page.*

class VipPaymentPage : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private var selectedRadioButtonText: String? = null
    private lateinit var radioButton: RadioButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vip_payment_page)

        sharedPreferences = this.getSharedPreferences(Constant.sharedkeyname, Context.MODE_PRIVATE)
        val annId = sharedPreferences.getInt("announcementId",0)
        val userId = sharedPreferences.getInt("userID",0)
        val token = sharedPreferences.getString("USERTOKENNN","")
        val authHeader = "Bearer $token"
        radioButton = findViewById(R.id.radioButton4Vip)
        backtoMainFromCourseUpload.setOnClickListener {
            val intent=Intent(this@VipPaymentPage,ProductDetailActivity::class.java)
            startActivity(intent)
        }

        println("Detail data: "+intent.getStringExtra("radiobuttonVip1"))
        val radiobutton1=intent.getStringExtra("radiobuttonVip1")
        val radiobutton2=intent.getStringExtra("radiobuttonVip2")
        val radiobutton3=intent.getStringExtra("radiobuttonVip3")
        radioButton1VIP.text=radiobutton1
        radioButton2VIP.text=radiobutton2
        radioButton3VIP.text=radiobutton3

        radioButton1VIP.setOnClickListener {
            selectedRadioButtonText=radioButton1VIP.text.toString()

        }
        radioButton2VIP.setOnClickListener {
            selectedRadioButtonText=radioButton2VIP.text.toString()

        }
        radioButton3VIP.setOnClickListener {
            selectedRadioButtonText=radioButton3VIP.text.toString()
        }

        nextPayForVip.setOnClickListener {
            if (radioGroupForVip.checkedRadioButtonId==-1){
                Toast.makeText(this, "Please select a price.", Toast.LENGTH_SHORT).show()
            }
            else if(!checkBoxForVip.isChecked){
                Toast.makeText(this, "Please agree to the terms.", Toast.LENGTH_SHORT).show()
            }else if (radioButton.isChecked ==false){
                Toast.makeText(this, "Please select payment method.", Toast.LENGTH_SHORT).show()
            }
            else{
                val intent= Intent(this@VipPaymentPage, EnterCardNumberPage::class.java)
                selectedRadioButtonText?.let {
                    intent.putExtra("selectedText", it)
                    startActivity(intent)
                }
            }

        }
    }
}