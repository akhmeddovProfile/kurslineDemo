package com.example.kurslinemobileapp.view.accountsFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.kurslinemobileapp.R
import kotlinx.android.synthetic.main.fragment_business_account.view.*

class BusinessAccountFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_business_account, container, false)
            view.backtoMainPageFromBusinessAcc.setOnClickListener {
                val fragmentManager = requireFragmentManager()
                // Start a fragment transaction
                val transaction = fragmentManager.beginTransaction()
                // Replace the first fragment with the second fragment
                transaction.replace(R.id.businessAccountRL, BusinessTransactionProfileFragment())
                transaction.setReorderingAllowed(true)
                // Add the transaction to the back stack
                transaction.addToBackStack(null)
                // Commit the transaction
                transaction.commit()
            }
        return view
    }
}