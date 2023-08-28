package com.example.kurslinemobileapp.view.payment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.kurslinemobileapp.R
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_card_number_page)

        println("Value: "+intent.getStringExtra("selectedText"))
        priceSum.text="${intent.getStringExtra("selectedText")}"

        val parts = priceSum.text.split("/")
        val pricePart = parts.last()
        val cleanedPricePart = pricePart.trim()

       // createOrder(cleanedPricePart)
// Remove any leading and trailing white spaces
        println("Before Encrypted: "+Constant.secretKey )
        val originalSecretKey = Constant.secretKey
        val secretKey = generateSecretKey(originalSecretKey)
        val iv = ByteArray(16)
        SecureRandom().nextBytes(iv)

       val encryptedData= encryptSecretKey(secretKey,iv)
        println("Encrypted data: $encryptedData")
        pay.setOnClickListener {
            createOrder()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createOrder(){
        val amount=0.01
        val totalAmount =amount
        val requestBody = CreateOrderRequestBody(totalAmount, "", "", "",
            "AZN", "", "This is Description", true, 0,
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

                        getStatusOrderMethod(apiService.payload.orderId,apiService.payload.sessionId)
                        return super.shouldOverrideUrlLoading(view, request)
                    }

                    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                        if (url != null && url.contains("approveURL")) {
                            // Handle payment success
                            handlePaymentSuccess()
                            return true
                        }
                        return super.shouldOverrideUrlLoading(view, url)
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
    private fun handlePaymentSuccess() {
        // Payment was successful, handle the result accordingly
        // For example, navigate to a success screen or update UI

        Toast.makeText(this,"Successfully",Toast.LENGTH_SHORT).show()
        // You might also navigate to a success screen
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getStatusOrderMethod(orderId:String, sessionId:String) {

        val requestBody = GetStatusOrderRequestBody("AZ", orderId, sessionId)
        val request = GetStatusOrderRequest(requestBody, "ES1092105")
        CoroutineScope(Dispatchers.Main).launch {
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