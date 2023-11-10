package com.example.kurslinemobileapp.view.loginRegister

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.app.kurslinemobileapp.R
import com.app.kurslinemobileapp.databinding.ActivityLoginBinding
import com.example.kurslinemobileapp.api.login.*
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.Constant.sharedkeyname
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.view.MainActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
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
     /*   bindinglogIn.createNewPassword.setOnClickListener {
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
        }*/


        bindinglogIn.loginButton.setOnClickListener {
           // val email =bindinglogIn.telephoneLoginEditText.text.toString().trim()
/*

            val password = bindinglogIn.passwordLoginEditText.text.toString()
            // Validate user input
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT)
                    .show()
            } else {
                showProgressButton(true)
                login(email, password)
            }
*/

            val telephoneNumber=bindinglogIn.telephoneLoginEditText.text.toString().trim()

            if (telephoneNumber.isEmpty()) {
                Toast.makeText(this, "Telefon nömrəsi boş ola bilməz", Toast.LENGTH_SHORT)
                    .show()
            }
            else{
                showBottomSheetDialogOTPNumber("+994"+telephoneNumber)
                logInWIthOTP("+994"+telephoneNumber)
                //showProgressButton(true)
            }
            }

    }

    private fun logInWIthOTP(telephone:String){
        println("Telephone number: "+ telephone)
        compositeDisposableLogin = CompositeDisposable()
        val retrofitService =
            RetrofitService(Constant.BASE_URL).retrofit.create(LogInAPi::class.java)
        val loginRequest=OTPRequest(telephone)
        compositeDisposableLogin!!.add(
            retrofitService.sendOTPCode(loginRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        println("isSuccess: "+it.isSuccess)
                        handleResponseOTP(it,telephone)
                    } ,
                    {throwable->
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
                    }
                )
        )
    }

    private fun handleResponseOTP(response:OTPResponse,number:String){
        val check=response.isSuccess
        if (check==true){
            //showBottomSheetDialogOTPNumber("+994"+number)
            Toast.makeText(
                this,
                "Təsdiqləmə kodu göndərildi.Bir neçə saniyə gözləyin",
                Toast.LENGTH_SHORT
            ).show()

            }

        else if(check==false){
            Toast.makeText(
                this,
                "Biraz sonra yenidən cəhd edin",
                Toast.LENGTH_SHORT
            ).show()
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

    private fun showBottomSheetDialogOTPNumber(number:String){
        Log.d("MyApp", "showBottomSheetDialogOTPNumber is called")
        val bottomSheetView = layoutInflater.inflate(R.layout.otp_page, null)
        val dialog = BottomSheetDialog(applicationContext)
        dialog.setContentView(bottomSheetView)
        val otpCode=bottomSheetView.findViewById<TextInputEditText>(R.id.OtpEditText)
        val buttonSendOtp=bottomSheetView.findViewById<AppCompatButton>(R.id.sendOTP)
        compositeDisposableLogin = CompositeDisposable()
        buttonSendOtp.setOnClickListener {
            val retrofitService =
                RetrofitService(Constant.BASE_URL).retrofit.create(LogInAPi::class.java)
            if (otpCode.text?.isEmpty()!!){
               val request=LoginOTPRequest(number,otpCode.text.toString())
                compositeDisposableLogin!!.add(
                    retrofitService.LoginWithOTP(request)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            this::loginresponseOTPCode
                        },{throwable->
                            if (throwable.message!!.contains("HTTP 401")) {
                                Toast.makeText(
                                    this,
                                    getString(R.string.infosWrong),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }else {
                                val text = getString(R.string.infosWrong)
                                Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
                            }
                            showProgressButton(false)

                        })
                )
            }else{


            }
        }
    }

    private fun loginresponseOTPCode(response: LogInResponseOTP){
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

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
        super.onBackPressed()
    }
}