package com.example.kurslinemobileapp.service

import android.content.Context
import android.content.SharedPreferences

val PREFERENCE_NAME="SharedPreferenceExample"
val PREFERENCE_LANGUAGE="az"

class MyPreference(context:Context) {
    val preference=context.getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE)

    fun getLangugae():String{
        return preference.getString(PREFERENCE_LANGUAGE,"az")!!
    }
    fun setLangugae(Language:String){
        val editor : SharedPreferences.Editor= preference.edit()
        editor.putString(PREFERENCE_LANGUAGE, Language)
        editor.apply()
    }
}