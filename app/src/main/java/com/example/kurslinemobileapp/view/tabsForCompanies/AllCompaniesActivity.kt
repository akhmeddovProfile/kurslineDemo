package com.example.kurslinemobileapp.view.tabsForCompanies

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.adapter.FragmentAdapterForTabLyout
import com.google.android.material.tabs.TabLayout

class AllCompaniesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_companies)

        var viewPager = findViewById(R.id.viewPager) as ViewPager
        var tablayout = findViewById(R.id.tablayout) as TabLayout

        val fragmentAdapter = FragmentAdapterForTabLyout(supportFragmentManager)
        fragmentAdapter.addFragment(TabforCompanies(), getString(R.string.kurslarText))
        fragmentAdapter.addFragment(TabforTeachers(), getString(R.string.tutorText))

        viewPager.adapter = fragmentAdapter
        tablayout.setupWithViewPager(viewPager)
    }
}