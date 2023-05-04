package com.example.kurslinemobileapp.UI

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.login.LogInAPi
import com.example.kurslinemobileapp.modelRegisterLogin.LogInResponse
import com.example.kurslinemobileapp.modelRegisterLogin.LoginRequestModel
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import io.reactivex.android.MainThreadDisposable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import kotlinx.android.synthetic.main.fragment_register.view.*

class LoginFragment : Fragment() {

    private var compositeDisposableLogin: CompositeDisposable? = null
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        val loginButton = view.findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            val email = emailLoginEditText.text.toString()
            val password = passwordLoginEditText.text.toString()
            // Validate user input
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT)
                    .show()
            } else {
                login(email, password)
                findNavController().navigate(R.id.action_loginFragment_to_accountFragment)
            }
        }
        return view
    }

    private fun login(email: String, password: String) {
        compositeDisposableLogin = CompositeDisposable()
        val retrofitService =
            RetrofitService(Constant.BASE_URL).retrofit.create(LogInAPi::class.java)
        val request = LoginRequestModel(email, password)
        compositeDisposableLogin!!.add(
            retrofitService.postLogin(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponseLogin, { throwable ->
                    println(throwable)
                })
        )
    }

    private fun handleResponseLogin(response: LogInResponse) {
        println("Response: " + response)
/*        val username = emailLoginEditText.text.toString()
        val password = passwordLoginEditText.text.toString()*/
        Toast.makeText(requireContext(), "Succesfully Login", Toast.LENGTH_SHORT).show()
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        sharedPreferences.edit().putString("token", response.accessToken.token).apply()
        editor.putBoolean("token", true)
        editor.apply()
/*        sharedPreferences.edit().putString("user_type", response.).apply()
        sharedPreferences.edit().putString("username", username).apply()
        sharedPreferences.edit().putString("password", password).apply()
        sharedPreferences.edit().putString("company", company).apply()
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()*/
    }
}