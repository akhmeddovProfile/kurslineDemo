package com.example.kurslinemobileapp.view.loginRegister

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.kurslinemobileapp.R
import com.app.kurslinemobileapp.databinding.ActivityLoginBinding
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
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.RequestBody

class LoginActivity : AppCompatActivity(),CoroutineScope by  MainScope() {
    private var compositeDisposableLogin: CompositeDisposable? = null
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var  bindinglogIn:ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindinglogIn= ActivityLoginBinding.inflate(layoutInflater)
        val view = bindinglogIn.root
        setContentView(view)
        //setContentView(R.layout.activity_login)

        bindinglogIn.goToRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, MainRegisterActivity::class.java)
            startActivity(intent)
        }
        bindinglogIn.createNewPassword.setOnClickListener {
            bindinglogIn.passwordLoginContainer.visibility=View.INVISIBLE
            bindinglogIn.loginButton.visibility=View.INVISIBLE
            bindinglogIn.enterEmailAddress.visibility=View.VISIBLE
            bindinglogIn.enterEmailAddress.setOnClickListener {
                val email = bindinglogIn.emailLoginEditText.text.toString()
                if(email.isEmpty()){
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT)
                        .show()
                }else{
                    resetPassword(email)
                    bindinglogIn.passwordLoginContainer.visibility=View.VISIBLE
                    bindinglogIn.loginButton.visibility=View.VISIBLE
                    bindinglogIn.enterEmailAddress.visibility=View.VISIBLE
                }

                showProgressButton(true)
            }
        }


        bindinglogIn.loginButton.setOnClickListener {
            val email = bindinglogIn.emailLoginEditText.text.toString()
            val password = bindinglogIn.passwordLoginEditText.text.toString()
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
                                getString(R.string.mailOrpassword),
                                Toast.LENGTH_SHORT
                            ).show()
                        }else {
                            val text = getString(R.string.infosWrong)
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
        editor.putString("userType",response.userInfo.userType)
        editor.putInt("userID",response.userInfo.id)
        editor.putString("USERTOKENNN", response.accessToken.token)
        editor.apply()
    }

    private fun showProgressButton(show: Boolean) {
        if (show) {
            bindinglogIn.loginButton.apply {
                isEnabled = false
                text = getString(R.string.loginCtn)  // Set empty text or loading indicator text
                // Add loading indicator drawable or ProgressBar if needed
            }
        } else {
            bindinglogIn.loginButton.apply {
                isEnabled = true
                text = getString(R.string.LOGIN)
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