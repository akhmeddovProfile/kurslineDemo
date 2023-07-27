package com.example.kurslinemobileapp.view.loginRegister

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.api.login.LoginResponseX
import com.example.kurslinemobileapp.api.register.RegisterAPI
import com.example.kurslinemobileapp.api.register.UserRegisterRequest
import com.example.kurslinemobileapp.api.register.UserRegisterResponse
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.view.MainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register_company.*
import kotlinx.android.synthetic.main.activity_user_register.*
import kotlinx.android.synthetic.main.activity_user_register.nameEditText
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.regex.Pattern

class UserRegisterActivity : AppCompatActivity() {
    private var block: Boolean = true
    private lateinit var sharedPreferences: SharedPreferences
    var compositeDisposable = CompositeDisposable()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_register)

        nameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used
            }

            override fun afterTextChanged(s: Editable?) {
                val name = s.toString().trim()
                val characterCount = name.length

                if (characterCount < 3 || characterCount > 50) {
                    nameContainer.error = "Name must be between 3 and 50 characters."
                } else {
                    nameContainer.error = null
                }

                characterCountTextViewUsername.text = "$characterCount / 50"
            }
        })

        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used
            }

            override fun afterTextChanged(s: Editable?) {
                val password = s.toString().trim()
                val isValid = isPasswordValid(password)

               if (!isValid) {
                    passwordContainer.error =
                        "Password must be at least 8 characters long and contain at least 1 uppercase letter, 1 lowercase letter, 1 digit, and 1 special character."
                } else {
                    passwordContainer.error = null
                }
            }
        })

        registerButton.setOnClickListener {
            block = true
            val name = nameEditText.text.toString().trim()
            val email = mailEditText.text.toString().trim()
            val phone =phoneEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            // Validate input fields
                // Save user registration data to shared preferences
                val sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putBoolean("is_registered", true)
                editor.apply()
                showProgressButton(true)
                register(name, email, "+994"+phone, password, "1")
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
            retrofit.createUser(fullname,emailAddress,phoneNumber,userPassword,gender)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse,
                    { throwable ->
                        if (throwable.message!!.contains("HTTP 409")){
                            Toast.makeText(this,"Bu nömrə və ya mail ünvanı artıq başqa istifadəçidə istifadə olunur",Toast.LENGTH_SHORT).show()
                        }else{
                            val text = "Məlumatlar doğru deyil"
                            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
                        }
                        showProgressButton(false) })
        )
    }

    private fun handleResponse(response: LoginResponseX) {
        println("Response: " + response)
        Toast.makeText(this,"Qeydiyyat uğurla tamamlandı",Toast.LENGTH_SHORT).show()
        val intent = Intent(this@UserRegisterActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
        sharedPreferences = this.getSharedPreferences(Constant.sharedkeyname, Context.MODE_PRIVATE)
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
            registerButton.apply {
                isEnabled = false
                text = "Hesab yaradılır..."  // Set empty text or loading indicator text
                // Add loading indicator drawable or ProgressBar if needed
            }
        } else {
            registerButton.apply {
                isEnabled = true
                text = "Qeydiyyatdan keç"
                // Restore original background, text color, etc., if modified
            }
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        val passwordPattern = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$"
        )
        return passwordPattern.matcher(password).matches()
    }
}
