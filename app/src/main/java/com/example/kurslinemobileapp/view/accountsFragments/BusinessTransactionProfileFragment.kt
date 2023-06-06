package com.example.kurslinemobileapp.view.accountsFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.view.fragments.FilterFragment
import kotlinx.android.synthetic.main.fragment_business_transactions_profile.view.*

class BusinessTransactionProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =
            inflater.inflate(R.layout.fragment_business_transactions_profile, container, false)

        val goButton = view.findViewById<ImageView>(R.id.goToBusinessAccountFragment)
        goButton.setOnClickListener {
            println("1")
            val fragmentManager = requireFragmentManager()
            // Start a fragment transaction
            val transaction = fragmentManager.beginTransaction()
            // Replace the first fragment with the second fragment
            transaction.replace(R.id.transactionProfileRl, BusinessAccountFragment())
            transaction.setReorderingAllowed(true)
            // Add the transaction to the back stack
            transaction.addToBackStack(null)
            // Commit the transaction
            transaction.commit()
        }
        return view
    }

}