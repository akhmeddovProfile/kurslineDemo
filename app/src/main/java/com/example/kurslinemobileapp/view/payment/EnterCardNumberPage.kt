package com.example.kurslinemobileapp.view.payment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.api.announcement.payment.sendOrderInfo.RequestOrderInfo
import com.example.kurslinemobileapp.api.paymentPayriff.createOrder.CreateOrderRequest
import com.example.kurslinemobileapp.api.paymentPayriff.createOrder.CreateOrderRequestBody
import com.example.kurslinemobileapp.api.paymentPayriff.getStatusOrder.GetStatusOrderRequest
import com.example.kurslinemobileapp.api.paymentPayriff.getStatusOrder.GetStatusOrderRequestBody
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.view.MainActivity
import kotlinx.android.synthetic.main.activity_enter_card_number_page.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class EnterCardNumberPage : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    var paymentState = false
    private lateinit var sharedPreferences: SharedPreferences
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_card_number_page)

 /*       println("Value: "+intent.getStringExtra("selectedText"))
        priceSum.text="${intent.getStringExtra("selectedText")}"

        val parts = priceSum.text.split("/")
        val pricePart = parts.last()
        val cleanedPricePart = pricePart.trim()
*/
       // createOrder(cleanedPricePart)
// Remove any leading and trailing white spaces
        println("Before Encrypted: "+Constant.secretKey )
        val originalSecretKey = Constant.secretKey
        val secretKey = generateSecretKey(originalSecretKey)
        val iv = ByteArray(16)
        SecureRandom().nextBytes(iv)
        val selectedPriceIdVip=intent.getIntExtra("selectedId",0)
        println("selectedPriceIdVip: $selectedPriceIdVip")
       val encryptedData= encryptSecretKey(secretKey,iv)
        println("Encrypted data: $encryptedData")

        createOrder()

    }

    @SuppressLint("SetJavaScriptEnabled")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createOrder(){
        sharedPreferences = this.getSharedPreferences(Constant.sharedkeyname, Context.MODE_PRIVATE)
        val annId = sharedPreferences.getInt("announcementId",0)
        val userId = sharedPreferences.getInt("userID",0)
        val token = sharedPreferences.getString("USERTOKENNN","")
        val authHeader = "Bearer $token"
        val selectedPriceIdVip=intent.getIntExtra("selectedId",0)
        val amount=intent.getDoubleExtra("selectedCost",1.5)
        val totalAmount =amount


       // val requestOrderInfo=RequestOrderInfo(annId)
        val requestBody = CreateOrderRequestBody(0.01, "https://www.youtube.com/", "", "",
            "AZN", "https://www.youtube.com/", "This is Description", true, 0,
            "BIRKART", "AZ", "")

        val request = CreateOrderRequest(requestBody, "ES1092105")

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val originalSecretKey = Constant.secretKey
                val secretKey = generateSecretKey(originalSecretKey)
                val iv = ByteArray(16)
                SecureRandom().nextBytes(iv)

                val encryptedData= encryptSecretKey(secretKey,iv)
                println("Encrypted data: $encryptedData")
                val apiService=RetrofitService(Constant.BASE_URL_PAYMENT).apiServicePaymentPayriff.createOrder(Constant.secretKey,"createOrder",request).await()
                paymentWebView.visibility = View.VISIBLE
                paymentWebView.loadUrl(apiService.payload.paymentUrl)
                paymentWebView.webViewClient=object :WebViewClient(){
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        val url = request?.url ?: return false
                        paymentWebView.loadUrl(url.toString())
                        println("url: "+url)
                        println("OrderId: "+apiService.payload.orderId)
                        println("SessionId: " + apiService.payload.sessionId)
                        getStatusOrderMethod(apiService.payload.orderId,apiService.payload.sessionId)

                        if(selectedPriceIdVip!=0){
                            postOrderInfoToServer(authHeader,userId,RequestOrderInfo(annId, ireliCekId = null,apiService.payload.orderId,apiService.payload.sessionId,selectedPriceIdVip))

                        }
                        return super.shouldOverrideUrlLoading(view, request)
                    }

                    override fun doUpdateVisitedHistory(
                        view: WebView?,
                        url: String?,
                        isReload: Boolean
                    ) {
                        super.doUpdateVisitedHistory(view, url, isReload)
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        println("Request error: "+request)
                        println("Error: "+error?.errorCode)
                        super.onReceivedError(view, request, error)
                    }
                }
                paymentWebView.settings.javaScriptEnabled = true

            }catch (e:HttpException){
                println("Code: "+e.code())
                println("Response: "+e.response())
            }catch (e:java.lang.Exception){
                println("Code: "+e.message)

            }
        }
    }
    /*announcementId:Int,orderId:String,sessionId:String,vipId:Int,ireliCekId:Int*/
    fun postOrderInfoToServer(token:String,userId:Int,createOrderRequest: RequestOrderInfo){
        CoroutineScope(Dispatchers.IO).launch {
            val apiservice=RetrofitService(Constant.BASE_URL).apipaymentpostorderinfo.postOrderIformation(userId,token,createOrderRequest).await()
            try {
                Log.d("MyTag","Success: ${apiservice.success}")
            }catch (e:HttpException){
                Log.d("MyTag","message:${e.message}, code: ${e.code()}")
            }
            catch (e:java.lang.Exception){
                Log.d("MyTag","${e.message}")
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getStatusOrderMethod(orderId:String, sessionId:String) {

        val requestBody = GetStatusOrderRequestBody("AZ", orderId, sessionId)
        val request = GetStatusOrderRequest(requestBody, "ES1092105")
        CoroutineScope(Dispatchers.IO).launch {
            val apiService=RetrofitService(Constant.BASE_URL_PAYMENT).apiservicePaymentGetOrderPayriff.getStatusOrder(Constant.secretKey,"getStatusOrder",request).await()
            try {
                if (apiService.payload.orderStatus.equals("DECLINED")){
                    paymentState=false
                }
                if (apiService.payload.orderStatus.equals("CANCELLED")){
                    paymentState=false

                }
                if (apiService.payload.orderStatus.equals("APPROVED")){
                    paymentState=true
                }
                paymentWebView.visibility = View.GONE
            }catch (e:HttpException){
                println("Code: "+e.code())
                println("Response: "+e.response())
            }catch (e:java.lang.Exception){
                println("Code: "+e.message)

            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun generateSecretKey(secretKey: String): SecretKey {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256, SecureRandom())
        return keyGen.generateKey()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun encryptSecretKey(secretKey: SecretKey ,iv: ByteArray): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(iv))

        val encryptedBytes = cipher.doFinal(secretKey.encoded)
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }
}