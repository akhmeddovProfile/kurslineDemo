package com.example.kurslinemobileapp.UI

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.kurslinemobileapp.R
import kotlinx.android.synthetic.main.fragment_main_register.view.*

class MainRegister : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_main_register, container, false)

        view.goToUserRegister.setOnClickListener {
            findNavController().navigate(R.id.action_mainRegister_to_registerFragment)
        }

        view.goToBusinessRegister.setOnClickListener {
            findNavController().navigate(R.id.action_mainRegister_to_businessRegister)
        }

        return view
    }
}