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
        if (name.isBlank() || surname.isBlank() || phone.isBlank() || email.isBlank() || password.isBlank() || password2.isBlank()) {
            // Show an error message and don't proceed to the next fragment
            Toast.makeText(requireContext(), "Please fill all the required fields", Toast.LENGTH_SHORT).show()
        } else if(password == password2){
            Toast.makeText(requireContext(), "Passwords don't match", Toast.LENGTH_SHORT).show()
        }else {
            // Save the user's account information to SharedPreferences and switch to the login fragment
            sharedPreferences.edit().apply {
                putString("name", name)
                putString("surname", surname)
                putString("phone", phone)
                putString("email", email)
                putString("password", password)
                apply()
            }

           view.registerButton.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }

        return view
    }

}