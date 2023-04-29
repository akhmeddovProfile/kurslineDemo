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
import com.example.kurslinemobileapp.model.mainpage.Highlight


class MainPageFragment : Fragment() {
    private lateinit var viewMain:View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewMain =  inflater.inflate(R.layout.fragment_main_page, container, false)

        imageforHighlight()
        return viewMain

    }

    private fun imageforHighlight(){
     val imageWithTextList= listOf(
         Highlight(R.drawable.mainpagehiglight,"Ən çox baxılanlar"),
         Highlight(R.drawable.yenielan,"")
     )
        val recylerviewForHighlight=viewMain.findViewById<RecyclerView>(R.id.recylerViewforHighlight)
        val adapter = HiglightForMainListAdapter(imageWithTextList)
        recylerviewForHighlight.adapter=adapter
        recylerviewForHighlight.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
    }
}