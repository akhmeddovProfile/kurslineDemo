package com.example.kurslinemobileapp.UI

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.kurslinemobileapp.R
import kotlinx.android.synthetic.main.fragment_account.view.*

class AccountFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        val button = view.findViewById<Button>(R.id.nextButton)
        button.setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_registerFragment)
        }

        val button2 = view.findViewById<Button>(R.id.nextLogin)
        button2.setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_loginFragment)
        }

        // Get the SharedPreferences object
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

// Get the saved account information from SharedPreferences
        val name = sharedPreferences.getString("name", "")
        val surname = sharedPreferences.getString("surname", "")
        val phone = sharedPreferences.getString("phone", "")
        val email = sharedPreferences.getString("email", "")

// Display the account information in the UI
        view.accountNameEditText.setText(name)
        view.accountSurnameEditText.setText(surname)
        view.accountPhoneEditText.setText(phone)
        view.accountMailEditText.setText(email)



        return view

    }
}