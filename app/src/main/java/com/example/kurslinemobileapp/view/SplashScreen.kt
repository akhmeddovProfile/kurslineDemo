package com.example.kurslinemobileapp.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.app.kurslinemobileapp.R
import com.app.kurslinemobileapp.databinding.ActivitySplashScreenBinding
import com.example.kurslinemobileapp.api.announcement.AnnouncementAPI
import com.example.kurslinemobileapp.model.mainpage.Highlight
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SplashScreen : AppCompatActivity() {
    private val splashDuration = 3000L
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var bindingSplashScreenBinding: ActivitySplashScreenBinding
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingSplashScreenBinding= ActivitySplashScreenBinding.inflate(layoutInflater)
        val view=bindingSplashScreenBinding.root
        setContentView(view)
        sharedPreferences =
           this.getSharedPreferences(Constant.sharedkeyname, Context.MODE_PRIVATE)


        compositeDisposable = CompositeDisposable()
        val retrofit =
            RetrofitService(Constant.BASE_URL).retrofit.create(AnnouncementAPI::class.java)
        compositeDisposable.add(
            retrofit.getHighlight().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ highlightModel ->
                    // The data has been successfully fetched from the server
                    // Now, you can update your imageWithTextList
                    val imageWithTextList = listOf(
                        Highlight(R.drawable.mainpage2,"${highlightModel[0].mostView} ${getString(R.string.mostViewHgText)}" ),
                        Highlight(R.drawable.yenielan2,"${highlightModel[0].newCourse} ${getString(R.string.newHgText)}"),
                        Highlight(R.drawable.vip, "${highlightModel[0].vip} ${getString(R.string.vipHgText)}")
                    )
                    val gson = Gson()
                    val imageWithTextListJson = gson.toJson(imageWithTextList)

// Save the JSON string in SharedPreferences
                    val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("imageWithTextList", imageWithTextListJson)
                    editor.apply()

                    // Use the updated imageWithTextList for your UI
                }, { error ->
                    // Handle the error if the request fails
                })
        )


        Handler().postDelayed({
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
            finish() // Close the splash screen activity
        }, splashDuration)
    }
}