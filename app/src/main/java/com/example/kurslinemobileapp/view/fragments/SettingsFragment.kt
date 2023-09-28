package com.example.kurslinemobileapp.view.fragments

import MyContextWrapper
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
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import com.example.kurslinemobileapp.service.MyPreference
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
    private val preference: MyPreference by lazy { MyPreference(requireContext()) }
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

    private fun spinnerLanguag(){
        val listofItems = arrayOf("az", "en")
        bindingSettings.languageSpinner.adapter=ArrayAdapter(requireActivity(),R.layout.spinnerlanguageitems,listofItems)
        val lang=preference.getLangugae()
        val index=listofItems.indexOf(lang)
        if (index>=0){
            bindingSettings.languageSpinner.setSelection(index)
        }
        bindingSettings.languageSpinner.onItemSelectedListener=object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                preference.setLangugae(listofItems[p2])
               val selectedItem= p0?.getItemAtPosition(p2).toString()
                if (selectedItem.equals("az")){
                    setAppLocale(requireContext(),selectedItem)
                    intent()
                }
                else if(selectedItem.equals("az")){
                    setAppLocale(requireContext(),selectedItem)
                    intent()
                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }
    fun intent(){
        val intent=Intent(requireContext(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
    private fun showChangeLanguage() {
        val listofItems = arrayOf("az", "en")
        val lang = preference.getLangugae()
        val currentLanguage = getCurrentLanguage(requireContext())
        val initiallyCheckedItem = if (currentLanguage == "az") 0 else 1

        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Choose Language")
            .setSingleChoiceItems(listofItems, initiallyCheckedItem) { dialog, which ->
                // Handle language selection
                val selectedLanguageCode = if (which == 0) "az" else "en"
                //preference.setLangugae(selectedLanguageCode)
                saveCurrentLanguage(requireActivity(),selectedLanguageCode)
            }.setPositiveButton("OK") { _, _ ->
                val selectedLanguage = getCurrentLanguage(requireActivity())
                // Change the app's language
                MyContextWrapper.wrap(requireContext(), selectedLanguage)

                // Restart the app to apply the new language
                val intent = requireActivity().baseContext.packageManager
                    .getLaunchIntentForPackage(requireActivity().baseContext.packageName)
                if (intent != null) {
                    intent.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_NEW_TASK
                    )
                    startActivity(intent)
                    requireActivity().finish()
                }
            }
            .setNegativeButton("Cancel") { _, _ -> }
            .create()

        alertDialog.show()
    }
    private fun updateAppLanguage(language: String) {
        val contextWrapper = MyContextWrapper.wrap(requireContext(), language)
        requireActivity().apply {
            val newContext = contextWrapper.baseContext
            recreate() // Recreate the activity to apply the new context
        }

    }
    fun setAppLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = context.resources
        val configuration = Configuration(resources.configuration)
        configuration.setLocale(locale)

        val displayMetrics = resources.displayMetrics
        context.resources.updateConfiguration(configuration, displayMetrics)
    }
    fun restartApp(context: Context) {
        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(context.packageName)
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            System.exit(0) // Ensure the app process is killed
        }
    }
    // Helper function to get the current language from shared preferences
    fun getCurrentLanguage(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        return sharedPreferences.getString("My_Lang", "az") ?: "az"
    }

    // Helper function to save the selected language to shared preferences
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
        // Create a new context with the updated configuration
        val contextWithLocale = requireContext().createConfigurationContext(config)

        // Use the updated context to update resources
        val updatedResources = contextWithLocale.resources
        // Update the configuration and resources in your app
        requireActivity().resources.updateConfiguration(config, updatedResources.displayMetrics)
       /* requireActivity().resources?.updateConfiguration(
            config,
            requireActivity().resources.displayMetrics
        )*/
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