package com.example.kurslinemobileapp.view.payment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.kurslinemobileapp.R
import kotlinx.android.synthetic.main.activity_vip_payment_page.*

class VipPaymentPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vip_payment_page)

        nextPay.setOnClickListener {
            if(radioButton4.isChecked==false){
                Toast.makeText(this,"Zehmet olmazsa qiymet secin",Toast.LENGTH_SHORT).show()
            }else{
                val intent=Intent(this,EnterCardNumberPage::class.java)
                startActivity(intent)
            }

        }
    }
}