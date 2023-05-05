package com.example.kurslinemobileapp.UI

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.example.kurslinemobileapp.AllCompaniesActivity
import com.example.kurslinemobileapp.R
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
                findNavController().navigate(R.id.action_settingsFragment_to_loginFragment)
            } else {
                // User is already registered, stay on the current fragment/activity
                findNavController().navigate(R.id.action_settingsFragment_to_accountFragment)
            }
        }

        view.allCoursesLl.setOnClickListener {
            val intent = Intent(requireContext(),AllCompaniesActivity::class.java)
            startActivity(intent)
        }
/*

        val language = sharedPreferences.getString("language", "DEFAULT")
        if(language.equals("av")){
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Language Selected")
            builder.setMessage("You have selected 'Azerbaijan' language.")
            builder.setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
            val alertDialog = builder.create()
            alertDialog.show()
        }
        if (language.equals("en")){
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Language Selected")
            builder.setMessage("You have selected 'English' language.")
            builder.setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
            val alertDialog = builder.create()
            alertDialog.show()
        }
*/

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

/*
    private fun setLocale(activity: Activity, languageCode: String?) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources: Resources = activity.resources
        val config: Configuration = resources.getConfiguration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.getDisplayMetrics())
    }
*/

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