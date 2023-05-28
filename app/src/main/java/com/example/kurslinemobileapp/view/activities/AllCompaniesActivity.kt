package com.example.kurslinemobileapp.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.adapter.FragmentAdapterForTabLyout
import com.example.kurslinemobileapp.view.fragments.TabforCompanies
import com.example.kurslinemobileapp.view.fragments.TabforTeachers
import com.google.android.material.tabs.TabLayout

class AllCompaniesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_companies)

        var viewPager = findViewById(R.id.viewPager) as ViewPager
        var tablayout = findViewById(R.id.tablayout) as TabLayout

        val fragmentAdapter = FragmentAdapterForTabLyout(supportFragmentManager)
        fragmentAdapter.addFragment(TabforCompanies(),"Kurslar")
        fragmentAdapter.addFragment(TabforTeachers(),"Repititorlar")

        viewPager.adapter = fragmentAdapter
        tablayout.setupWithViewPager(viewPager)
    }
}