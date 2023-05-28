package com.example.kurslinemobileapp.view.accountsFragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.view.loginRegister.LoginActivity
import com.example.kurslinemobileapp.view.loginRegister.RegisterCompanyActivity
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
            val intent = Intent(activity, LoginActivity::class.java)
           activity?.startActivity(intent)
            activity?.finish()
        } else {
            // User is already registered, stay on the current fragment/activity
            Toast.makeText(requireContext(), "Already logged", Toast.LENGTH_SHORT).show()
        }

        view.goToBusinessCreate.setOnClickListener {
            val intent = Intent(requireContext(), RegisterCompanyActivity::class.java)
            startActivity(intent)
        }
        // Display the account information in the UI

        view.backtoMainPage.setOnClickListener {
           // findNavController().navigate(R.id.action_accountFragment_to_mainPageFragment)
        }

        return view
    }
}