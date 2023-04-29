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
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.kurslinemobileapp.R
import kotlinx.android.synthetic.main.fragment_register.view.*

class RegisterFragment : Fragment() {


    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        // Get the SharedPreferences object
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

// Get the user's input data
        val name = view.nameEditText.text.toString()
        val surname = view.surnameEditText.text.toString()
        val phone = view.phoneEditText.text.toString()
        val email = view.mailEditText.text.toString()
        val password = view.passwordEditText.text.toString()
        val password2 = view.confirmPasswordEditText.text.toString()
// Save the user's account information to SharedPreferences

            // Save the user's account information to SharedPreferences and switch to the login fragment
            sharedPreferences.edit().apply {
                putString("name", name)
                putString("surname", surname)
                putString("phone", phone)
                putString("email", email)
                putString("password", password)
                apply()
                val button = view.findViewById<Button>(R.id.registerButton)
                button.setOnClickListener {
                    findNavController().navigate(R.id.action_registerFragment_to_accountFragment)
                }
            }


        return view
    }

}