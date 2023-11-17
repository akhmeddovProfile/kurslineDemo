package com.example.kurslinemobileapp.service

import android.content.Context
import android.content.res.Resources
import android.os.Build
import java.util.*

object MiuiLocaleHelper {
    fun setLocale(context: Context, language: String): Context {
        val resources: Resources = context.resources
        val locale = Locale(language)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val configuration = resources.configuration
            configuration.setLocale(locale)
            return context.createConfigurationContext(configuration)
        } else {
            val configuration = resources.configuration
            configuration.locale = locale
            resources.updateConfiguration(configuration, resources.displayMetrics)
            return context
        }
    }
}