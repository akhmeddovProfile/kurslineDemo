package com.example.kurslinemobileapp.UI

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.adapter.HiglightForMainListAdapter
import com.example.kurslinemobileapp.adapter.MainListProductAdapter
import com.example.kurslinemobileapp.model.mainpage.Highlight
import com.example.kurslinemobileapp.model.mainpage.Product


class MainPageFragment : Fragment() {
    private lateinit var viewMain:View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewMain =  inflater.inflate(R.layout.fragment_main_page, container, false)

        imageforHighlight()
        getProducts()
        return viewMain

    }

    private fun imageforHighlight(){
     val imageWithTextList= listOf(
         Highlight(R.drawable.mainpagehiglight,"Ən çox baxılanlar"),
         Highlight(R.drawable.yenielan,"1345 yeni kurs")
     )
        val recylerviewForHighlight=viewMain.findViewById<RecyclerView>(R.id.recylerViewforHighlight)
        val adapter = HiglightForMainListAdapter(imageWithTextList)
        recylerviewForHighlight.adapter=adapter
        recylerviewForHighlight.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
    }

    private fun getProducts(){
        val productInformation= listOf(
            Product("Online",
                R.drawable.vip_icon,
                R.drawable.yenielan,
                "Mobile Programming",
                "Aim Tech",
                "This course for test")
        )
        val recyclerviewForProducts=viewMain.findViewById<RecyclerView>(R.id.recylerViewForProductList)
        val adapter=MainListProductAdapter(productInformation)
        recyclerviewForProducts.adapter=adapter
        recyclerviewForProducts.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
    }
}