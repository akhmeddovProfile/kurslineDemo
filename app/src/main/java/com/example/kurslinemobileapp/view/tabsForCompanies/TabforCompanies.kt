package com.example.kurslinemobileapp.view.tabsForCompanies

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.adapter.CompanyTeacherAdapter
import com.example.kurslinemobileapp.api.companyTeachers.CompanyTeacherAPI
import com.example.kurslinemobileapp.api.companyTeachers.companyTeacherRow.CompanyTeacherModel
import com.example.kurslinemobileapp.api.companyTeachers.companyTeacherRow.CompanyTeacherModelItem
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlin.collections.ArrayList

class TabforCompanies : Fragment() {
    private lateinit var companyTeacherAdapter: CompanyTeacherAdapter
    private lateinit var mainList: ArrayList<CompanyTeacherModelItem>
    private lateinit var compositeDisposable: CompositeDisposable
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_tabfor_companies, container, false)
        mainList = ArrayList()
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerviewForCompanyteacher)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        companyTeacherAdapter = CompanyTeacherAdapter(mainList)
        recyclerView.adapter = companyTeacherAdapter
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

    private fun handleResponse(response: CompanyTeacherModel) {
        mainList.addAll(response)
        companyTeacherAdapter.notifyDataSetChanged()
    }
}