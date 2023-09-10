package com.example.kurslinemobileapp.view.payment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.app.kurslinemobileapp.R
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
    private var selectedRadioButtonText: String? = null
    private  var selectedCost:Double?=null
    private var selectedButtonId:Int?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = this.getSharedPreferences(Constant.sharedkeyname, Context.MODE_PRIVATE)
        setContentView(R.layout.activity_move_forward_ann)
        val annId = sharedPreferences.getInt("announcementId",0)
        val userId = sharedPreferences.getInt("userID",0)
        val token = sharedPreferences.getString("USERTOKENNN","")
        val authHeader = "Bearer $token"
        backtoDetailFromMoveForward.setOnClickListener {
            val intent= Intent(this@MoveForwardAnn, ProductDetailActivity::class.java)
            startActivity(intent)
        }
        println("Detail data: "+intent.getStringExtra("radiobuttonMoveFrw1"))
        val radiobutton1=intent.getStringExtra("radiobuttonMoveFrw1")
        val radiobutton2=intent.getStringExtra("radiobuttonMoveFrw2")
        radioButtonMovefor1.text=radiobutton1
        radioButtonMovefor2.text=radiobutton2
        val costbutton1=intent.getDoubleExtra("redioBtncost1",1.00)
        val costbutton2=intent.getDoubleExtra("redioBtncost2",2.00)
        val radiobuttonid1=intent.getIntExtra("moveId1",1)
        val radiobuttonid2=intent.getIntExtra("moveId2",2)
        radioButtonMovefor1.setOnClickListener {
            selectedRadioButtonText=radioButtonMovefor1.text.toString()
            selectedCost=costbutton1
            selectedButtonId=radiobuttonid1
        }
        radioButtonMovefor2.setOnClickListener {
            selectedRadioButtonText=radioButtonMovefor2.text.toString()
            selectedCost=costbutton2
            selectedButtonId=radiobuttonid2
        }

        nextPayEnterCardNumber.setOnClickListener {
            if (radiogroupformoveforward.checkedRadioButtonId==-1){
                Toast.makeText(this, "Please select a price.", Toast.LENGTH_SHORT).show()
            }
            else if(!checkBoxMoveForward.isChecked){
                Toast.makeText(this, "Please agree to the terms.", Toast.LENGTH_SHORT).show()
            }
            else if (radioButton4.isChecked ==false){
                Toast.makeText(this, "Please select payment method.", Toast.LENGTH_SHORT).show()
            }
            else{
                val intent= Intent(this@MoveForwardAnn, EnterCardNumberPage::class.java)
                selectedRadioButtonText?.let {
                    intent.putExtra("selectedText", it)
                }
                selectedCost?.let { it->
                    intent.putExtra("selectedCost", it)
                }
                selectedButtonId?.let { it->
                    intent.putExtra("selectedId",it)
                }
                startActivity(intent)

            }

        }

    }
    private fun sendToAnotherActivity(intent:Intent,selectedText: String) {
        intent.putExtra("selectedText", selectedText)
    }

}