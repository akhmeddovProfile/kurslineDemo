package com.example.kurslinemobileapp.view.tabsForCompanies

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.kurslinemobileapp.adapter.CompanyTeacherAdapter
import com.example.kurslinemobileapp.api.companyTeachers.CompanyTeacherAPI
import com.example.kurslinemobileapp.api.companyTeachers.companyTeacherRow.CompanyTeacherModel
import com.example.kurslinemobileapp.api.companyTeachers.companyTeacherRow.CompanyTeacherModelItem
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class TabforCompanies : Fragment(), androidx.appcompat.widget.SearchView.OnQueryTextListener {
    private lateinit var view : ViewGroup
    private lateinit var companyTeacherAdapter: CompanyTeacherAdapter
    private lateinit var mainList: ArrayList<CompanyTeacherModelItem>
    private lateinit var compositeDisposable: CompositeDisposable
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         view = inflater.inflate(com.app.kurslinemobileapp.R.layout.fragment_tabfor_companies, container, false) as ViewGroup
        val recycler = view.findViewById<RecyclerView>(com.app.kurslinemobileapp.R.id.recyclerviewForCompanyteacher)
        recycler.visibility = View.GONE
        val lottie = view.findViewById<LottieAnimationView>(com.app.kurslinemobileapp.R.id.loadingTabCompany)
        lottie.visibility = View.VISIBLE
        lottie.playAnimation()

        mainList = ArrayList()
        recycler.layoutManager = LinearLayoutManager(requireContext())
        companyTeacherAdapter = CompanyTeacherAdapter(mainList, object : CompanyTeacherAdapter.VoiceCallToCourses {
            override fun clickOnCall(number: String, position: Int) {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$number")
                requireContext().startActivity(intent)
            }
        })
        recycler.adapter = companyTeacherAdapter


        val searchView = view.findViewById<androidx.appcompat.widget.SearchView>(com.app.kurslinemobileapp.R.id.searchViewCompanyEditText)
        val searchEditText = searchView?.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)

        // Check if the searchEditText is not null before setting the text color
        searchEditText?.setTextColor(ContextCompat.getColor(requireContext(), com.app.kurslinemobileapp.R.color.black))
        searchEditText?.setHintTextColor(ContextCompat.getColor(requireContext(), com.app.kurslinemobileapp.R.color.grayColor2))
        searchView.setOnQueryTextListener(this)


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
        val recycler = view.findViewById<RecyclerView>(com.app.kurslinemobileapp.R.id.recyclerviewForCompanyteacher)
        recycler.visibility = View.VISIBLE
        val lottie = view.findViewById<LottieAnimationView>(com.app.kurslinemobileapp.R.id.loadingTabCompany)
        lottie.visibility = View.GONE
        lottie.pauseAnimation()
        response.get(0).companyPhone
        val filteredList = response.filter { it.companyStatusId == 1 }
        println("New: "+filteredList)
        mainList.addAll(filteredList)
        companyTeacherAdapter.notifyDataSetChanged()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        companyTeacherAdapter.getFilter().filter(newText)
        return false
    }
/*
    override fun clickOnCall(number: String, position: Int) {
        if(number.isNotEmpty()){
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$number")
            startActivity(intent)
        }
        else{
            Toast.makeText(requireContext(),"Bu kursun mobile nomresi yoxdur",Toast.LENGTH_SHORT).show()
        }

    }*/

}