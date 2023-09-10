package com.example.kurslinemobileapp.view.accountsFragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.app.kurslinemobileapp.R
import com.example.kurslinemobileapp.service.Constant.sharedkeyname
import com.example.kurslinemobileapp.view.loginRegister.LoginActivity

class BlankAccountFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_blank_account, container, false)
        val sharedPreferences = requireActivity().getSharedPreferences(sharedkeyname, Context.MODE_PRIVATE)
        // Get the saved account information from SharedPreferences
        val isRegistered = sharedPreferences.getBoolean("token", false)
        if (!isRegistered) {
            // User is not registered, navigate to the registration fragment
            val intent = Intent(activity, LoginActivity::class.java)
            activity?.startActivity(intent)
            activity?.finish()
        } else {
            val userType = sharedPreferences.getString("userType",null)
            // User is already registered, stay on the current fragment/activity
            if (userType == "İstifadəçi") {
// User is not registered, navigate to the registration fragment
                val fragmentManager = requireFragmentManager()
                // Start a fragment transaction
                val transaction = fragmentManager.beginTransaction()

                // Replace the first fragment with the second fragment
                transaction.replace(R.id.frameLayoutforChange, UserAccountFragment())
                transaction.setReorderingAllowed(true)

                // Add the transaction to the back stack
                transaction.addToBackStack(null)
                // Commit the transaction
                transaction.commit()
// User is already registered, stay on the current fragment/activity
            } else if(userType == "Kurs") {
                // Required data is present, display it
                val fragmentManager = requireFragmentManager()

                // Start a fragment transaction
                val transaction = fragmentManager.beginTransaction()

                // Replace the first fragment with the second fragment
                transaction.replace(R.id.frameLayoutforChange, BusinessTransactionProfileFragment())
                transaction.setReorderingAllowed(true)

                // Add the transaction to the back stack
                transaction.addToBackStack(null)

                // Commit the transaction
                transaction.commit()
            }else if(userType == "Repititor") {
                // Required data is present, display it
                val fragmentManager = requireFragmentManager()

                // Start a fragment transaction
                val transaction = fragmentManager.beginTransaction()

                // Replace the first fragment with the second fragment
                transaction.replace(R.id.frameLayoutforChange, BusinessTransactionProfileFragment())
                transaction.setReorderingAllowed(true)

                // Add the transaction to the back stack
                transaction.addToBackStack(null)

                // Commit the transaction
                transaction.commit()
            } else{
                val intent = Intent(activity, LoginActivity::class.java)
                activity?.startActivity(intent)
            }
        }

        return view
    }
}