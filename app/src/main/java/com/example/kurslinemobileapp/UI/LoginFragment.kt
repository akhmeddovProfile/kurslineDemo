package com.example.kurslinemobileapp.UI

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.kurslinemobileapp.R
import kotlinx.android.synthetic.main.fragment_login.view.*
import kotlinx.android.synthetic.main.fragment_register.view.*

class LoginFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_login, container, false)
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        // Get the user's input data
        val email = view.emailLoginEditText.text.toString()
        val password = view.passwordLoginEditText.text.toString()

// Check if any of the required fields are empty
            // Get the saved account information from SharedPreferences
            val savedEmail = sharedPreferences.getString("email", "")
            val savedPassword = sharedPreferences.getString("password", "")

            // Check if the login is successful
            if (email == savedEmail && password == savedPassword) {
                // Login successful, switch to the account fragment
                view.loginButton.setOnClickListener {
                    findNavController().navigate(R.id.action_loginFragment_to_accountFragment)
                }
            } else {
                // Login failed, show an error message
                Toast.makeText(requireContext(), "Invalid email or password", Toast.LENGTH_SHORT).show()
            }


        return view
    }

}