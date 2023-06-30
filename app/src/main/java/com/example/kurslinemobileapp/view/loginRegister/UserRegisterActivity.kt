package com.example.kurslinemobileapp.view.loginRegister

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.api.register.RegisterAPI
import com.example.kurslinemobileapp.api.register.UserRegisterRequest
import com.example.kurslinemobileapp.api.register.UserRegisterResponse
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_register_company.*
import kotlinx.android.synthetic.main.activity_user_register.*
import kotlinx.android.synthetic.main.activity_user_register.nameEditText
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class UserRegisterActivity : AppCompatActivity() {
    private var block: Boolean = true
    var compositeDisposable = CompositeDisposable()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_register)

        registerButton.setOnClickListener {
            block = true
            val name = nameEditText.text.toString().trim()
            val email = mailEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            // Validate input fields
            if (name.isEmpty()) {
                nameEditText.error = " Name required"
                nameEditText.requestFocus()
                block = false
            }
            if (email.isEmpty()) {
                mailEditText.error = " Email required"
                mailEditText.requestFocus()
                 block = false
            }
            if (phone.isEmpty()) {
                phoneEditText.error = " Phone required"
                phoneEditText.requestFocus()
                block = false
            }
            if (password.isEmpty()) {
                passwordEditText.error = " Password required"
                passwordEditText.requestFocus()
                block = false
            } else {
                // Save user registration data to shared preferences
                val sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putBoolean("is_registered", true)
                editor.apply()
                register(name, email, phone, password, "1")
            }
        }
    }

    private fun register(
        username: String,
        email: String,
        phone: String,
        password: String,
        userGender: String
    ) {

        val fullname: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), username)
        val emailAddress: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), email)
        val phoneNumber: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), phone)
        val userPassword: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), password)
        val gender: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), userGender)

        compositeDisposable = CompositeDisposable()
        val retrofit =
            RetrofitService(Constant.BASE_URL).retrofit.create(RegisterAPI::class.java)
        compositeDisposable.add(
            retrofit.createAPI(fullname,emailAddress,phoneNumber,userPassword,gender)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse,
                    { throwable ->
                    println(throwable) })
        )
    }

    private fun handleResponse(response: UserRegisterResponse) {
        println("Response: " + response)
        val intent = Intent(this@UserRegisterActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
