package com.example.kurslinemobileapp.view.accountsFragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.view.fragments.FilterFragment
import kotlinx.android.synthetic.main.fragment_business_transactions_profile.view.*

class BusinessTransactionProfileFragment : Fragment() {
    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var button4: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =
            inflater.inflate(R.layout.fragment_business_transactions_profile, container, false)

        button1 = view.findViewById(R.id.button1BusinessTrans)
        button2 = view.findViewById(R.id.button2BusinessTrans)
        button3 = view.findViewById(R.id.button3BusinessTrans)
        button4 = view.findViewById(R.id.button4BusinessTrans)

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

        button1.setOnClickListener {
            updateButtonBackgrounds(button1)
        }
        button2.setOnClickListener {
            updateButtonBackgrounds(button2)
        }
        button3.setOnClickListener {
            updateButtonBackgrounds(button3)
        }
        button4.setOnClickListener {
            updateButtonBackgrounds(button4)
        }
        return view
    }

    @SuppressLint("ResourceAsColor")
    private fun updateButtonBackgrounds(selectedButton: Button) {
        val buttons = arrayOf(button1, button2, button3, button4)

        for (button in buttons) {
            if (button == selectedButton) {
                // Set selected button background
                button.setBackgroundResource(R.drawable.business_button_bg)
                button.setTextColor(R.color.white)
            } else {
                // Set unselected button background
                button.setBackgroundResource(R.drawable.business_button_bg_2)
                button.setTextColor(R.color.black)
            }
        }

    }
}