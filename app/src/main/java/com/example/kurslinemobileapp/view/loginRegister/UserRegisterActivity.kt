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
import kotlinx.android.synthetic.main.activity_user_register.*

class UserRegisterActivity : AppCompatActivity() {
    var compositeDisposable = CompositeDisposable()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_register)

        registerButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val email = mailEditText.text.toString()
            val phone = phoneEditText.text.toString()
            val password = passwordEditText.text.toString()
            val password2 = confirmPasswordEditText.text.toString()
            // Validate input fields
            if (name.isEmpty() ||   email.isEmpty() || password.isEmpty() || password2.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT)
                    .show()
            } else if (password != password2) {
                Toast.makeText(this, "Passwords is not equal", Toast.LENGTH_SHORT)
                    .show()
            } else {
                // Save user registration data to shared preferences
                val sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putBoolean("is_registered", true)
                editor.apply()
                register(name, email, phone, password, 1)
            }
        }
    }

    private fun register(
        username: String,
        email: String,
        phone: String,
        password: String,
        gender: Int
    ) {
        compositeDisposable = CompositeDisposable()
        val retrofit =
            RetrofitService(Constant.BASE_URL).retrofit.create(RegisterAPI::class.java)
        val request = UserRegisterRequest(username, email, phone, password, gender)
        compositeDisposable.add(
            retrofit.createAPI(request)
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
