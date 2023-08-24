package com.example.kurslinemobileapp.view.payment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.api.announcement.payment.priceMoveForward.MoveforwardPriceResponseX
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.service.Room.moveforwardinfo.MoveForwardDao
import com.example.kurslinemobileapp.service.Room.moveforwardinfo.MoveForwardEntity
import com.example.kurslinemobileapp.view.courseFmAc.ProductDetailActivity
import kotlinx.android.synthetic.main.activity_move_forward_ann.*
import kotlinx.android.synthetic.main.activity_vip_payment_page.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MoveForwardAnn : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var moveforwardList:ArrayList<MoveforwardPriceResponseX>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = this.getSharedPreferences(Constant.sharedkeyname, Context.MODE_PRIVATE)
        moveforwardList= ArrayList<MoveforwardPriceResponseX>()
        setContentView(R.layout.activity_move_forward_ann)
        val annId = sharedPreferences.getInt("announcementId",0)
        val userId = sharedPreferences.getInt("userID",0)
        val token = sharedPreferences.getString("USERTOKENNN","")
        val authHeader = "Bearer $token"
        backtoDetailFromMoveForward.setOnClickListener {
            val intent= Intent(this@MoveForwardAnn, ProductDetailActivity::class.java)
            startActivity(intent)
        }
        //getPriceforMoveForward(userId,annId,authHeader)
    }


}