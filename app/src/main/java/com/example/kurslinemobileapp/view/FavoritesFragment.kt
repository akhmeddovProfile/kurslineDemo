package com.example.kurslinemobileapp.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.kurslinemobileapp.R
import kotlinx.android.synthetic.main.fragment_favorites.view.*

class FavoritesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)

        val sharedPreferences =
            requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isRegistered = sharedPreferences.getBoolean("token", false)

        if (!isRegistered) {
// User is not registered, navigate to the registration fragment
            findNavController().navigate(R.id.action_favoritesFragment_to_loginFragment)
// User is already registered, stay on the current fragment/activity
        } else {
            // Required data is present, display it
            view.favoritesRl.visibility = View.VISIBLE
        }
        return view
    }

}