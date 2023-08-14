package com.example.kurslinemobileapp.view.payment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.view.courseFmAc.ProductDetailActivity
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
        backtoMainFromCourseUpload.setOnClickListener {
            val intent=Intent(this@VipPaymentPage,ProductDetailActivity::class.java)
            startActivity(intent)
        }

    }
}