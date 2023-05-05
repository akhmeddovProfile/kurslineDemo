package com.example.kurslinemobileapp.UI

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.kurslinemobileapp.AllCompaniesActivity
import com.example.kurslinemobileapp.R
import kotlinx.android.synthetic.main.fragment_settings.view.*


class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_settings, container, false)

        val sharedPreferences =
            requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Get the saved account information from SharedPreferences
        val isRegistered = sharedPreferences.getBoolean("token", false)
        view.goToMyAccount.setOnClickListener {
            if (!isRegistered) {
                // User is not registered, navigate to the registration fragment
                findNavController().navigate(R.id.action_settingsFragment_to_loginFragment)
            } else {
                // User is already registered, stay on the current fragment/activity
                findNavController().navigate(R.id.action_settingsFragment_to_accountFragment)
            }
        }

        view.allCoursesLl.setOnClickListener {
            val intent = Intent(requireContext(),AllCompaniesActivity::class.java)
            startActivity(intent)
        }
        return view
    }
}