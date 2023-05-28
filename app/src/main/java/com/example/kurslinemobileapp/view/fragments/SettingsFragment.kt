package com.example.kurslinemobileapp.view.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.view.tabsForCompanies.AllCompaniesActivity
import com.example.kurslinemobileapp.view.loginRegister.LoginActivity
import kotlinx.android.synthetic.main.fragment_settings.view.*
import java.util.Locale


class SettingsFragment : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_settings, container, false)

        val sharedPreferences =
            requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        loadLocate()

        // Get the saved account information from SharedPreferences
        val isRegistered = sharedPreferences.getBoolean("token", false)
        view.goToMyAccount.setOnClickListener {
            if (!isRegistered) {
                // User is not registered, navigate to the registration fragment
                val intent = Intent(activity, LoginActivity::class.java)
              activity?.startActivity(intent)
                activity?.finish()
            } else {
                // User is already registered, stay on the current fragment/activity
                findNavController().navigate(R.id.action_settingsFragment_to_accountFragment)
            }
        }

        view.allCoursesLl.setOnClickListener {
            val intent = Intent(requireContext(), AllCompaniesActivity::class.java)
            startActivity(intent)
        }


        view.languageMode.setOnClickListener {
            showChangeLanguage()
        }
        return view
    }

    private fun showChangeLanguage() {
        val listofItems= arrayOf("Azərbaycan","English")

        val mBuilder=AlertDialog.Builder(requireContext())
        mBuilder.setTitle("Choose Language")
        mBuilder.setSingleChoiceItems(listofItems,-1){dialog,which->
            if (which==0){
            setLocate("av")
                Toast.makeText(requireContext(),"Azərbaycan dilinə dəyişdirildi",Toast.LENGTH_SHORT).show()
                requireActivity().recreate()

            }
            else if(which==1){
                setLocate("en")
                Toast.makeText(requireContext(),"Application language change to English",Toast.LENGTH_SHORT).show()
                requireActivity().recreate()
            }
            dialog.dismiss()
        }
        val mdialog=mBuilder.create()
        mdialog.show()
    }


    @SuppressLint("SuspiciousIndentation")
    private fun setLocate (Lang: String) {
        val locale =Locale (Lang)
        Locale.setDefault(locale)
        val config = Configuration ()
        config.locale= locale
                requireActivity().resources?.updateConfiguration (config, requireActivity().resources.displayMetrics)
        val editor = context?.getSharedPreferences("Settings",Context.MODE_PRIVATE)?.edit()
        editor?.putString ("My_Lang", Lang)
        editor?.apply()
    }
    private fun loadLocate () {

        val sharedPreferences =context?.getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val language =sharedPreferences?.getString( "My_Lang","")
        setLocate(language!!)
    }
}