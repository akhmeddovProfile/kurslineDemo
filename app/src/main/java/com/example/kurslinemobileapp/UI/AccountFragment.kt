package com.example.kurslinemobileapp.UI

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
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

        // Get the SharedPreferences object
        val sharedPreferences =
            requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Get the saved account information from SharedPreferences
        val isRegistered = sharedPreferences.getBoolean("token", false)
        if (!isRegistered) {
            // User is not registered, navigate to the registration fragment
            findNavController().navigate(R.id.action_accountFragment_to_registerFragment)
        } else {
            // User is already registered, stay on the current fragment/activity
            Toast.makeText(requireContext(), "Already logged", Toast.LENGTH_SHORT).show()
        }

        view.goToBusinessCreate.setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_businessRegister)
        }
        // Display the account information in the UI

        view.backtoMainPage.setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_mainPageFragment)
        }

        return view
    }
}