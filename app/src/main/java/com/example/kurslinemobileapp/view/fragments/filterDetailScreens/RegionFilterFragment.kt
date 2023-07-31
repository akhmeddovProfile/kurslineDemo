package com.example.kurslinemobileapp.view.fragments.filterDetailScreens

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.adapter.CategoryAdapter
import com.example.kurslinemobileapp.adapter.RegionAdapter
import com.example.kurslinemobileapp.api.companyData.CompanyDatasAPI
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_filter.view.*

class RegionFilterFragment : Fragment() {
    private lateinit var view: ViewGroup
    var compositeDisposable = CompositeDisposable()
    private lateinit var regionAdapter: RegionAdapter
    lateinit var regionId:String
    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         view =  inflater.inflate(R.layout.fragment_region_filter, container, false) as ViewGroup

        regionId = ""
            getRegions()
        return view

    }

    private fun getRegions(){
        val recyclerViewCategories: RecyclerView =
            view.findViewById(R.id.filterRegionRV)
        recyclerViewCategories.setHasFixedSize(true)
        recyclerViewCategories.setLayoutManager(LinearLayoutManager(requireContext()))
        compositeDisposable = CompositeDisposable()
        val retrofit =
            RetrofitService(Constant.BASE_URL).retrofit.create(CompanyDatasAPI::class.java)
        compositeDisposable.add(
            retrofit.getRegions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ regions ->
                    println("1")
                    regionAdapter = RegionAdapter(regions.regions)
                    recyclerViewCategories.adapter = regionAdapter
                    recyclerViewCategories.isNestedScrollingEnabled=false
                    regionAdapter.setChanged(regions.regions)
                    regionAdapter.setOnItemClickListener { region ->
                        regionId = region.regionId.toString()
                    }
                }, { throwable -> println("MyTests: $throwable") })
        )

    }

}