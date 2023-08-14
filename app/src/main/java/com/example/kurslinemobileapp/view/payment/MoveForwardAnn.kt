package com.example.kurslinemobileapp.view.payment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.view.courseFmAc.ProductDetailActivity
import kotlinx.android.synthetic.main.activity_move_forward_ann.*
import kotlinx.android.synthetic.main.activity_vip_payment_page.*

class MoveForwardAnn : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_move_forward_ann)

        backtoDetailFromMoveForward.setOnClickListener {
            val intent= Intent(this@MoveForwardAnn, ProductDetailActivity::class.java)
            startActivity(intent)
        }
    }
}