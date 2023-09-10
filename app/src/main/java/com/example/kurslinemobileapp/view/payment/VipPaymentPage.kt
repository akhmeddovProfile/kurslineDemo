package com.example.kurslinemobileapp.view.payment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import com.app.kurslinemobileapp.R
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.view.courseFmAc.ProductDetailActivity
import kotlinx.android.synthetic.main.activity_move_forward_ann.*
import kotlinx.android.synthetic.main.activity_vip_payment_page.*

class VipPaymentPage : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private var selectedRadioButtonText: String? = null
    private var selectedPrice:Double?=null
    private var selectedButtonId:Int?=null
    private lateinit var radioButton: RadioButton
    @SuppressLint("MissingInflatedId")
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
        val radiobuttonid1=intent.getIntExtra("vipId1",1)
        val radiobuttonid2=intent.getIntExtra("vipId2",2)
        val radiobuttonid3=intent.getIntExtra("vipId3",3)
        val elaninfofromintent=intent.getStringExtra("elanInfo")
        radioButton1VIP.text=radiobutton1
        radioButton2VIP.text=radiobutton2
        radioButton3VIP.text=radiobutton3
        val costbutton1=intent.getDoubleExtra("radiobuttonVipCost1",1.00)
        val costbutton2=intent.getDoubleExtra("radiobuttonVipCost2",2.00)
        println("getting" + costbutton2
        )
        val costbutton3=intent.getDoubleExtra("radiobuttonVipCost3",3.00)

        radioButton1VIP.setOnClickListener {
            selectedRadioButtonText=radioButton1VIP.text.toString()
            selectedPrice = costbutton1
            selectedButtonId=radiobuttonid1
        }
        radioButton2VIP.setOnClickListener {
            selectedRadioButtonText=radioButton2VIP.text.toString()
            selectedPrice = costbutton2
            selectedButtonId=radiobuttonid2
        }
        radioButton3VIP.setOnClickListener {
            selectedRadioButtonText=radioButton3VIP.text.toString()
            selectedPrice = costbutton3
            selectedButtonId=radiobuttonid3
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
                }
                selectedPrice?.let { it->
                    intent.putExtra("selectedCost", it)
                }
                selectedButtonId?.let { it->
                    intent.putExtra("selectedId",it)
                }
                startActivity(intent)

            }

        }
    }
}