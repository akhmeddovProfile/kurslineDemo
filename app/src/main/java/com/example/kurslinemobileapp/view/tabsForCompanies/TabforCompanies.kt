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
import kotlin.collections.ArrayList

class TabforCompanies : Fragment() {
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
         view = inflater.inflate(R.layout.fragment_tabfor_companies, container, false) as ViewGroup
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerviewForCompanyteacher)
        recycler.visibility = View.GONE
        val lottie = view.findViewById<LottieAnimationView>(R.id.loadingTabCompany)
        lottie.visibility = View.VISIBLE
        lottie.playAnimation()
        mainList = ArrayList()
        recycler.layoutManager = LinearLayoutManager(requireContext())
        companyTeacherAdapter = CompanyTeacherAdapter(mainList)
        recycler.adapter = companyTeacherAdapter

        searchView = view.findViewById(R.id.searchViewCompanyEditText)
        searchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                val filteredList = if (query.isNotEmpty()) {
                    mainList.filter { it.companyName.contains(query, ignoreCase = true) }
                } else {
                    ArrayList(mainList)
                }
                companyTeacherAdapter.updateList(filteredList)
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
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerviewForCompanyteacher)
        recycler.visibility = View.VISIBLE
        val lottie = view.findViewById<LottieAnimationView>(R.id.loadingTabCompany)
        lottie.visibility = View.GONE
        lottie.pauseAnimation()
        val filteredList = response.filter { it.companyStatusId == 1 }
        mainList.addAll(filteredList)
        companyTeacherAdapter.notifyDataSetChanged()
    }

    private fun filterCompanies(query: String) {
        val filteredList = if (query.isNotEmpty()) {
            mainList.filter { it.companyName.contains(query, ignoreCase = true) }
        } else {
            mainList
        }
        companyTeacherAdapter.updateList(filteredList)
    }


}