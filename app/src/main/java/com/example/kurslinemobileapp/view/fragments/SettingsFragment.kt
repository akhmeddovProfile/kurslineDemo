package com.example.kurslinemobileapp.view.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.kurslinemobileapp.R
import com.app.kurslinemobileapp.databinding.FragmentSettingsBinding
import com.example.kurslinemobileapp.adapter.ContactUsAdapter
import com.example.kurslinemobileapp.model.ContactItem
import com.example.kurslinemobileapp.service.Constant

import com.example.kurslinemobileapp.view.AboutActivity
import com.example.kurslinemobileapp.view.MainActivity
import com.example.kurslinemobileapp.view.PdfPrivacyPolicy
import com.example.kurslinemobileapp.view.loginRegister.LoginActivity
import com.example.kurslinemobileapp.view.tabsForCompanies.AllCompaniesActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*


class SettingsFragment : Fragment() {
    private lateinit var contactAdapter: ContactUsAdapter
    private lateinit var contactList: List<ContactItem>
    private lateinit var bindingSettings:FragmentSettingsBinding
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingSettings=FragmentSettingsBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        //val view = inflater.inflate(R.layout.fragment_settings, container, false)
        //contactAdapter=ContactUsAdapter(contactList)
        val sharedPreferences =
            requireActivity().getSharedPreferences(Constant.sharedkeyname, Context.MODE_PRIVATE)

        //loadLocate()
        bindingSettings.privacySettingsId.setOnClickListener {
            val intent = Intent(requireContext(), PdfPrivacyPolicy::class.java)
            startActivity(intent)
        }

        bindingSettings.aboutSettingsId.setOnClickListener {
            val intent = Intent(requireContext(), AboutActivity::class.java)
            startActivity(intent)
        }

        // Get the saved account information from SharedPreferences
        val isRegistered = sharedPreferences.getBoolean("token", false)
        bindingSettings.goToMyAccount.setOnClickListener {
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
        bindingSettings.allCoursesLl.setOnClickListener {
            val intent = Intent(requireContext(), AllCompaniesActivity::class.java)
            startActivity(intent)
        }


        bindingSettings.languageMode.setOnClickListener {
            showChangeLanguage()
        }

        val userType = sharedPreferences.getString("userType",null)
        if (userType == "İstifadəçi" || userType == "Kurs" || userType == "Repititor" ){
            bindingSettings.exitCourseLL.visibility = View.VISIBLE
        }else{
            bindingSettings.exitCourseLL.visibility = View.GONE
        }

        bindingSettings.exitCourseLL.setOnClickListener {
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
        bindingSettings.helpLl.setOnClickListener {
            showBottomSheedDialogHelp()
        }

        return bindingSettings.root
    }

    private fun showBottomSheedDialogHelp(){
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_dialog_contact, null)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(bottomSheetView)
        val recyclerviewContact: RecyclerView =
            bottomSheetView.findViewById(R.id.recyclerViewContacts)
        recyclerviewContact.setHasFixedSize(true)
        recyclerviewContact.setLayoutManager(LinearLayoutManager(requireContext()))
        recyclerviewContact.layoutManager = LinearLayoutManager(requireContext())

        contactList= listOf(
            ContactItem("Məktub yazın",R.drawable.mailnew),
            ContactItem("Instagram",R.drawable.instagramnew),
            ContactItem("Facebook",R.drawable.facebooknew)
        )
        contactAdapter = ContactUsAdapter(contactList)
        recyclerviewContact.adapter = contactAdapter
        dialog.show()
    }

    private fun clearSharedPreferences(){
        val sharedPreferences =
            requireActivity().getSharedPreferences(Constant.sharedkeyname, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
        val intent=Intent(requireContext(),MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun showChangeLanguage() {
        val listofItems = arrayOf("Azərbaycan", "English")
        val mBuilder = AlertDialog.Builder(requireContext())
        mBuilder.setTitle("Choose Language")
        mBuilder.setSingleChoiceItems(listofItems, -1) { dialog, which ->
            var selectedLanguage:String="az"

            if (which == 0) {
                selectedLanguage="az"
                setLocate("az")
                Toast.makeText(
                    requireContext(),
                    "Azərbaycan dilinə dəyişdirildi",
                    Toast.LENGTH_SHORT
                ).show()
                requireActivity().recreate()

            } else if (which == 1) {
                selectedLanguage="en"
                setLocate("en")
                Toast.makeText(
                    requireContext(),
                    "Application language change to English",
                    Toast.LENGTH_SHORT
                ).show()
                requireActivity().recreate()
            }
            if (isXiaomiDevice()) {
             //   val updatedContext = MiuiLocaleHelper.setLocale(requireContext(), selectedLanguage)

                // Update your UI with the new context
                // Example: textView.text = updatedContext.getString(R.string.my_string)
            }

            // Save the selected language to shared preferences
            saveCurrentLanguage(requireContext(), selectedLanguage)

            dialog.dismiss()
        }
        val mdialog = mBuilder.create()
        mdialog.show()
    }
    fun setLocale(context: Context, languageCode: String) {
        val resources = context.resources
        val configuration = resources.configuration
        val locale = Locale(languageCode)
        configuration.setLocale(locale)
        context.createConfigurationContext(configuration)
    }
    private fun isXiaomiDevice(): Boolean {
        // Check if the device manufacturer is Xiaomi
        val manufacturer = android.os.Build.MANUFACTURER
        return manufacturer.equals("Xiaomi", ignoreCase = true)
    }
    fun saveCurrentLanguage(context: Context, languageCode: String) {
        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("My_Lang", languageCode)
        editor.apply()
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
