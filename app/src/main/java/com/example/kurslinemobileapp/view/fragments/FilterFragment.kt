package com.example.kurslinemobileapp.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.api.announcement.AnnouncementAPI
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.GetAllAnnouncement
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import io.reactivex.Observable
import io.reactivex.android.MainThreadDisposable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_filter.*
import kotlinx.android.synthetic.main.fragment_filter.view.*

class FilterFragment : Fragment() {
    var compositeDisposable = CompositeDisposable()
    lateinit var items:MutableList<GetAllAnnouncement>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_filter, container, false)
        view.backtoMainPage.setOnClickListener {
            findNavController().navigate(R.id.action_filterFragment_to_homeFragment)
        }

        view.showCoursesFilterBtn.setOnClickListener {
            filteredItems()
        }

        items= mutableListOf()
        return view
    }

    @SuppressLint("CheckResult")
    fun filteredItems(){
        compositeDisposable= CompositeDisposable()
        val retrofit=RetrofitService(Constant.BASE_URL).retrofit.create(AnnouncementAPI::class.java)

        val limit = 10
        val offset = 0
        val regionId = 1
        val categoryId = 1
        val search = "sasa"
        val minPrice = 120.0
        val maxPrice = 500.0
        val statusId = 2
        val isOnlineId = 1
        val userId = 15

        retrofit.getFilteredItems(
            limit,
            offset,
            regionId,
            categoryId,
            search,
            minPrice,
            maxPrice,
            statusId,
            isOnlineId,
            userId
        ).flatMap {
            Observable.fromIterable(it)
        }.flatMapIterable { announcmenet->
            announcmenet.announcemenets
        }.filter { item->
            item.isOnline==isOnlineId.toString()&&
            item.price>=minPrice&&
            item.price<=maxPrice
        }.toList()
            .subscribe(
                {filteritems->
                    println("FilterItems: "+filteritems)
                },
                {error->

                }
            )
    }
}