package com.example.kurslinemobileapp.view.loginRegister

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.api.login.LogInAPi
import com.example.kurslinemobileapp.api.login.LoginRequest
import com.example.kurslinemobileapp.api.login.LoginResponseX
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.Constant.sharedkeyname
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.view.MainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private var compositeDisposableLogin: CompositeDisposable? = null
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        goToRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, MainRegisterActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val email = emailLoginEditText.text.toString()
            val password = passwordLoginEditText.text.toString()
            // Validate user input
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT)
                    .show()
            } else {
                login(email, password)
            }
        }
    }

    private fun login(email: String, password: String) {
        compositeDisposableLogin = CompositeDisposable()
        val retrofitService =
            RetrofitService(Constant.BASE_URL).retrofit.create(LogInAPi::class.java)
        val request = LoginRequest(email, password)
        compositeDisposableLogin!!.add(
            retrofitService.postLogin(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponseLogin,
                    { throwable ->
                        val text = throwable.toString()
                    Toast.makeText(this,text,Toast.LENGTH_SHORT).show()
                })
        )
    }

    private fun handleResponseLogin(response: LoginResponseX) {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
        println("Response: " + response)
        println("userId: " + response.userInfo.id)
        Toast.makeText(this, "Succesfully Login", Toast.LENGTH_SHORT).show()
        sharedPreferences = this.getSharedPreferences(sharedkeyname, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        sharedPreferences.edit().putString("token", response.accessToken.token).apply()
        editor.putBoolean("token", true)
        editor.putBoolean("isFavorite",response.userInfo.isFavorite)
        editor.putString("userType",response.userInfo.userType)
        editor.putInt("userID",response.userInfo.id)
        editor.putString("USERTOKENNN", response.accessToken.token)
        editor.apply()
    }
}