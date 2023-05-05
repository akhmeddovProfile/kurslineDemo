package com.example.kurslinemobileapp.UI

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.api.register.RegisterRequest
import com.example.kurslinemobileapp.api.register.RegisterResponse
import com.example.kurslinemobileapp.api.register.RegisterAPI
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_register.view.*

class RegisterFragment : Fragment() {
    var compositeDisposable = CompositeDisposable()

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        val registerButton = view.findViewById<Button>(R.id.registerButton)
        registerButton.setOnClickListener {
            val name = view.nameEditText.text.toString()
            val surname = view.surnameEditText.text.toString()
            val email = view.mailEditText.text.toString()
            val phone = view.phoneEditText.text.toString()
            val password = view.passwordEditText.text.toString()
            val password2 = view.confirmPasswordEditText.text.toString()
            // Validate input fields
            if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty() || password2.isEmpty() || phone.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT)
                    .show()
            } else if (password != password2) {
                Toast.makeText(requireContext(), "Passwords is not equal", Toast.LENGTH_SHORT)
                    .show()
            } else {
                // Save user registration data to shared preferences
                val sharedPreferences =
                    requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putBoolean("is_registered", true)
                editor.apply()
                register(name, email, phone, password, 1)
            }
        }


        return view
    }

    private fun register(
        username: String,
        email: String,
        phone: String,
        password: String,
        gender: Int
    ) {
        compositeDisposable = CompositeDisposable()
        val retrofit = RetrofitService(Constant.BASE_URL).retrofit.create(RegisterAPI::class.java)
        val request = RegisterRequest(username, email, phone, password, gender)
        compositeDisposable?.add(retrofit.createAPI(request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResponse, { throwable -> println(throwable) })
        )
    }

    private fun handleResponse(response: RegisterResponse) {
        println("Response: " + response)
        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
    }
}