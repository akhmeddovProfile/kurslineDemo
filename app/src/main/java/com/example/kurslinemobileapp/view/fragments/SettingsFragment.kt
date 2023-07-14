package com.example.kurslinemobileapp.view.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.view.MainActivity
import com.example.kurslinemobileapp.view.accountsFragments.BusinessTransactionProfileFragment
import com.example.kurslinemobileapp.view.accountsFragments.UserAccountFragment
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
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

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
                val userType = sharedPreferences.getString("userType",null)
                // User is already registered, stay on the current fragment/activity
                if (userType == "İstifadəçi") {
                    findNavController().navigate(R.id.action_settingsFragment_to_accountFragment)
                } else if(userType == "Kurs") {
                    findNavController().navigate(R.id.action_settingsFragment_to_businessAccountFragment)
                } else if(userType == "Repititor") {
                    findNavController().navigate(R.id.action_settingsFragment_to_businessAccountFragment)
                }else{
                    println("User not found")
                }
            }
        }

        view.allCoursesLl.setOnClickListener {
            val intent = Intent(requireContext(), AllCompaniesActivity::class.java)
            startActivity(intent)
        }


        view.languageMode.setOnClickListener {
            showChangeLanguage()
        }

        view.exitCourseLL.setOnClickListener {
            val alertDialogBuilder=android.app.AlertDialog.Builder(ContextThemeWrapper(requireContext(),R.style.CustomAlertDialogTheme))
            alertDialogBuilder.setMessage("Are you sure you want to exit Kursline Application?")
            alertDialogBuilder.setPositiveButton("Yes"){dialog, which->
                clearSharedPreferences()
            }
            alertDialogBuilder.setNegativeButton("No"){dialog, which->

            }
            val alertDialog =alertDialogBuilder.create()
            alertDialog.show()
        }


        return view
    }

    private fun clearSharedPreferences(){
        val sharedPreferences =
            requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
        val intent=Intent(requireContext(),MainActivity::class.java)
        startActivity(intent)
    }
    private fun showChangeLanguage() {
        val listofItems = arrayOf("Azərbaycan", "English")
        val mBuilder = AlertDialog.Builder(requireContext())
        mBuilder.setTitle("Choose Language")
        mBuilder.setSingleChoiceItems(listofItems, -1) { dialog, which ->
            if (which == 0) {
                setLocate("av")
                Toast.makeText(
                    requireContext(),
                    "Azərbaycan dilinə dəyişdirildi",
                    Toast.LENGTH_SHORT
                ).show()
                requireActivity().recreate()

            } else if (which == 1) {
                setLocate("en")
                Toast.makeText(
                    requireContext(),
                    "Application language change to English",
                    Toast.LENGTH_SHORT
                ).show()
                requireActivity().recreate()
            }
            dialog.dismiss()
        }
        val mdialog = mBuilder.create()
        mdialog.show()
    }


    @SuppressLint("SuspiciousIndentation")
    private fun setLocate(Lang: String) {
        val locale = Locale(Lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        requireActivity().resources?.updateConfiguration(
            config,
            requireActivity().resources.displayMetrics
        )
        val editor = context?.getSharedPreferences("Settings", Context.MODE_PRIVATE)?.edit()
        editor?.putString("My_Lang", Lang)
        editor?.apply()
    }

    private fun loadLocate() {
        val sharedPreferences = context?.getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val language = sharedPreferences?.getString("My_Lang", "")
        setLocate(language!!)
    }
}