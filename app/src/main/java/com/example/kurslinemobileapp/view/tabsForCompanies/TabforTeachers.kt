package com.example.kurslinemobileapp.view.tabsForCompanies

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.adapter.CompanyTeacherAdapter
import com.example.kurslinemobileapp.api.companyTeachers.CompanyTeacherAPI
import com.example.kurslinemobileapp.api.companyTeachers.companyTeacherRow.CompanyTeacherModel
import com.example.kurslinemobileapp.api.companyTeachers.companyTeacherRow.CompanyTeacherModelItem
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.google.android.material.textfield.TextInputEditText
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_tabfor_companies.view.*
import kotlinx.android.synthetic.main.fragment_tabfor_teachers.view.*


class TabforTeachers : Fragment(),CompanyTeacherAdapter.VoiceCallToCourses {
    private lateinit var view : ViewGroup
    private lateinit var companyTeacherAdapter: CompanyTeacherAdapter
    private lateinit var mainList: ArrayList<CompanyTeacherModelItem>
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var searchView: TextInputEditText
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         view = inflater.inflate(R.layout.fragment_tabfor_teachers, container, false) as ViewGroup
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerViewForTeacher)
        recycler.visibility = View.GONE
        val lottie = view.findViewById<LottieAnimationView>(R.id.loadingTabTeacher)
        lottie.visibility = View.VISIBLE
        lottie.playAnimation()
        mainList = ArrayList()
        recycler.layoutManager = LinearLayoutManager(requireContext())
        companyTeacherAdapter = CompanyTeacherAdapter(mainList,this@TabforTeachers)
        recycler.adapter = companyTeacherAdapter

        view.searchViewForTeachers.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(msg: String): Boolean {
                companyTeacherAdapter.getFilter().filter(msg)
                return false
            }
        })

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
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerViewForTeacher)
        recycler.visibility = View.VISIBLE
        val lottie = view.findViewById<LottieAnimationView>(R.id.loadingTabTeacher)
        lottie.visibility = View.GONE
        lottie.playAnimation()
        val filteredList = response.filter { it.companyStatusId == 2 }
        mainList.addAll(filteredList)
        companyTeacherAdapter.notifyDataSetChanged()
    }

    override fun clickOnCall(number: String, position: Int) {

    }
}