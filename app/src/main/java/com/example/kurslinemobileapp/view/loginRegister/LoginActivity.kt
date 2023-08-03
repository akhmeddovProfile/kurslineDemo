package com.example.kurslinemobileapp.view.loginRegister

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.api.login.LogInAPi
import com.example.kurslinemobileapp.api.login.LoginRequest
import com.example.kurslinemobileapp.api.login.LoginResponseX
import com.example.kurslinemobileapp.api.login.ResetPasswordResponse
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.Constant.sharedkeyname
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.view.MainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import kotlin.coroutines.CoroutineContext

class LoginActivity : AppCompatActivity(),CoroutineScope by  MainScope() {
    private var compositeDisposableLogin: CompositeDisposable? = null
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        goToRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, MainRegisterActivity::class.java)
            startActivity(intent)
        }
        createNewPassword.setOnClickListener {
            passwordLoginContainer.visibility=View.INVISIBLE
            loginButton.visibility=View.INVISIBLE
            enterEmailAddress.visibility=View.VISIBLE
            enterEmailAddress.setOnClickListener {
                val email = emailLoginEditText.text.toString()
                if(email.isEmpty()){
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT)
                        .show()
                }else{
                    resetPassword(email)
                    passwordLoginContainer.visibility=View.VISIBLE
                    loginButton.visibility=View.VISIBLE
                    enterEmailAddress.visibility=View.VISIBLE
                }

                showProgressButton(true)
            }
        }


        loginButton.setOnClickListener {
            val email = emailLoginEditText.text.toString()
            val password = passwordLoginEditText.text.toString()
            // Validate user input
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT)
                    .show()
            } else {
                showProgressButton(true)
                login(email, password)
            }
        }
    }

    private fun resetPassword(email:String){
        launch(Dispatchers.Main) {
            try {
                val retrofitService=RetrofitService(Constant.BASE_URL).apiService.resetPassword(RequestBody.create(null,email)).await()
                handleResetPasswordResponse(retrofitService)
            }catch (e:java.lang.Exception){
                handleError(e)
            }
        }
    }
    private fun handleResetPasswordResponse(response: ResetPasswordResponse) {
        // Handle the API response here based on the isSuccess value
        if (response.isSuccess) {
            Toast.makeText(this,"Password reset email sent your Mail address successfully",Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this,"Failed to send password reset email.",Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleError(error: Throwable) {
        // Handle the error here
        if(error.message!!.contains("HTTP 403")){
            Toast.makeText(
                this,
                "Try again later. Because you sent email so much",
                Toast.LENGTH_SHORT
            ).show()
        }
        Toast.makeText(this,"Error: ${error.message}",Toast.LENGTH_SHORT).show()
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
                        if (throwable.message!!.contains("HTTP 401")) {
                            Toast.makeText(
                                this,
                                "Mail ünvanı və ya parol yanlışdır",
                                Toast.LENGTH_SHORT
                            ).show()
                        }else {
                            val text = "Məlumatlar doğru deyil"
                            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
                        }
                        showProgressButton(false)
                    })
        )
    }

    private fun handleResponseLogin(response: LoginResponseX) {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
        println("Response: " + response)
        println("userId: " + response.userInfo.id)
        sharedPreferences = this.getSharedPreferences(sharedkeyname, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        sharedPreferences.edit().putString("token", response.accessToken.token).apply()
        editor.putBoolean("token", true)
/*
        editor.putBoolean("isFavorite",response.userInfo.isFavorite)
*/
        editor.putString("userType",response.userInfo.userType)
        editor.putInt("userID",response.userInfo.id)
        editor.putString("USERTOKENNN", response.accessToken.token)
        editor.apply()
    }

    private fun showProgressButton(show: Boolean) {
        if (show) {
            loginButton.apply {
                isEnabled = false
                text = "Hesaba daxil olunur..."  // Set empty text or loading indicator text
                // Add loading indicator drawable or ProgressBar if needed
            }
        } else {
            loginButton.apply {
                isEnabled = true
                text = "Daxil Olun"
                // Restore original background, text color, etc., if modified
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
        super.onBackPressed()
    }
}