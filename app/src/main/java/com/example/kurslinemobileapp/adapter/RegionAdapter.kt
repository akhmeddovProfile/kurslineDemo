package com.example.kurslinemobileapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.api.companyData.Category
import com.example.kurslinemobileapp.api.companyData.Region

class RegionAdapter (var regions: List<Region>) :
    RecyclerView.Adapter<RegionAdapter.RegionViewHolder>() {
    private var onItemClickListener: ((Region) -> Unit)? = null

    fun setOnItemClickListener(listener: (Region) -> Unit) {
        onItemClickListener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.filter_details_layout, parent, false)
        return RegionViewHolder(view)
    }

    override fun onBindViewHolder(holder: RegionViewHolder, position: Int) {
        val region = regions[position]
        holder.bind(region)
    }

    override fun getItemCount(): Int {
        return regions.size
    }
    fun setChanged(regions: List<Region>){
        this.regions = regions
        notifyDataSetChanged()
    }
    inner class RegionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewName: TextView = itemView.findViewById(R.id.filterRegionText)

        @SuppressLint("ResourceAsColor")
        fun bind(region: Region) {
            textViewName.setTextColor(R.color.black)
            textViewName.text = region.regionName
            itemView.setOnClickListener {
                onItemClickListener?.invoke(region)
            }
        }
    }
}