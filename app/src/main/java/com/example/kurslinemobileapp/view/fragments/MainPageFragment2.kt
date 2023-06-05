package com.example.kurslinemobileapp.view.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.adapter.HiglightForMainListAdapter
import com.example.kurslinemobileapp.adapter.MainListProductAdapter
import com.example.kurslinemobileapp.api.announcement.AnnouncementAPI
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.Announcemenet
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.GetAllAnnouncement
import com.example.kurslinemobileapp.model.mainpage.Highlight
import com.example.kurslinemobileapp.model.mainpage.Product
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.view.accountsFragments.UserAccountFragment
import com.example.kurslinemobileapp.view.courseFmAc.ProductDetailActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_main_page2.view.*

class MainPageFragment2 : Fragment(){
    private lateinit var viewMain: View
    private lateinit var mainListProductAdapter: MainListProductAdapter
    private lateinit var mainList : ArrayList<GetAllAnnouncement>
    private lateinit var mainList2 : ArrayList<GetAllAnnouncement>
    private lateinit var compositeDisposable: CompositeDisposable
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewMain = inflater.inflate(R.layout.fragment_main_page2, container, false)

        mainList = ArrayList<GetAllAnnouncement>()
        mainList2 = ArrayList<GetAllAnnouncement>()
        viewMain.recylerViewForProductList.layoutManager = GridLayoutManager(requireContext(),2)
        getProducts()
        imageforHighlight()
        viewMain.textinputfilter.setOnClickListener {
            val fragmentManager = requireFragmentManager()
            // Start a fragment transaction
            val transaction = fragmentManager.beginTransaction()

            // Replace the first fragment with the second fragment
            transaction.replace(R.id.frameLayoutforChange, FilterFragment())
            transaction.setReorderingAllowed(true)

            // Add the transaction to the back stack
            transaction.addToBackStack(null)
            // Commit the transaction
            transaction.commit()
        }
        return viewMain
    }

    private fun getProducts(){
        compositeDisposable = CompositeDisposable()
        val retrofit = RetrofitService(Constant.BASE_URL).retrofit.create(AnnouncementAPI::class.java)
        compositeDisposable.add(retrofit.getAnnouncement()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResponse, { throwable-> println("MyTests: $throwable") }))
    }

    private fun handleResponse(response : GetAllAnnouncement){
        mainList.addAll(listOf(response))
        mainList2.addAll(listOf(response))
        println("responseElan: " + response)
        mainListProductAdapter = MainListProductAdapter(mainList2)
        val recyclerviewForProducts =
            viewMain.findViewById<RecyclerView>(R.id.recylerViewForProductList)
        recyclerviewForProducts.adapter = mainListProductAdapter
        mainListProductAdapter.notifyDataSetChanged()
    }

    private fun imageforHighlight() {
        val imageWithTextList = listOf(
            Highlight(R.drawable.mainpagehiglight, "Ən çox baxılanlar"),
            Highlight(R.drawable.yenielan, "1345 yeni kurs")
        )
        val recylerviewForHighlight =
            viewMain.findViewById<RecyclerView>(R.id.recylerViewforHighlight)
        val adapter = HiglightForMainListAdapter(imageWithTextList)
        recylerviewForHighlight.adapter = adapter
        recylerviewForHighlight.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

}