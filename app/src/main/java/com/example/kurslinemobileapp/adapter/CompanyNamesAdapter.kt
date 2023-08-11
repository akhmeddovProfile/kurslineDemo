package com.example.kurslinemobileapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.api.companyTeachers.companyTeacherRow.CompanyTeacherModelItem

class CompanyNamesAdapter(
    private val companyNames: List<CompanyTeacherModelItem>,
    private val itemClickListener: (String,Int) -> Unit
) : RecyclerView.Adapter<CompanyNamesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_company_name, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val companyName = companyNames[position]
        holder.bind(companyName)
    }

    override fun getItemCount(): Int {
        return companyNames.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(response: CompanyTeacherModelItem) {
            val companyNameTextView: TextView = itemView.findViewById(R.id.companyNameTextView)
            companyNameTextView.text = response.companyName
            itemView.setOnClickListener {
                itemClickListener.invoke(response.companyName,response.companyId)
            }
        }
    }
}
