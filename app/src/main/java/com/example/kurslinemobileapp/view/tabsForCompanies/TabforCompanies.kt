package com.example.kurslinemobileapp.view.tabsForCompanies

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.adapter.CompanyTeacherAdapter
import com.example.kurslinemobileapp.adapter.MainListProductAdapter
import com.example.kurslinemobileapp.api.announcement.AnnouncementAPI
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.GetAllAnnouncement
import com.example.kurslinemobileapp.api.companyTeachers.CompanyTeacherAPI
import com.example.kurslinemobileapp.api.companyTeachers.CompanyTeacherModel
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.view.courseFmAc.CourseBusinessProfile
import com.example.kurslinemobileapp.view.courseFmAc.ProductDetailActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class TabforCompanies : Fragment() {
    private lateinit var companyTeacherAdapter: CompanyTeacherAdapter
    private lateinit var mainList : ArrayList<CompanyTeacherModel>
    private lateinit var mainList2 : ArrayList<CompanyTeacherModel>
    private lateinit var compositeDisposable: CompositeDisposable
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_tabfor_companies, container, false)
        mainList = ArrayList<CompanyTeacherModel>()
        mainList2 = ArrayList<CompanyTeacherModel>()
        val coursesRV = view.findViewById<RecyclerView>(R.id.recyclerviewForCompanyteacher)
        coursesRV.layoutManager = LinearLayoutManager(requireContext())
        getCompanies()
        return view
    }

    private fun getCompanies(){
        compositeDisposable = CompositeDisposable()
        val retrofit = RetrofitService(Constant.BASE_URL).retrofit.create(CompanyTeacherAPI::class.java)
        compositeDisposable.add(retrofit.getCompanies()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResponse, { throwable-> println("MyCompanies: $throwable") }))
    }

    private fun handleResponse(response : CompanyTeacherModel){
        mainList.addAll(listOf(response))
        mainList2.addAll(listOf(response))
        println("companies: " + response)
        companyTeacherAdapter = CompanyTeacherAdapter(mainList2)
        val recyclerviewForCompanies = requireView().findViewById<RecyclerView>(R.id.recyclerviewForCompanyteacher)
        recyclerviewForCompanies.adapter = companyTeacherAdapter
        companyTeacherAdapter.notifyDataSetChanged()
        companyTeacherAdapter.setOnItemClickListener {
            val intent = Intent(activity, CourseBusinessProfile::class.java)
            activity?.startActivity(intent)
        }
    }
}