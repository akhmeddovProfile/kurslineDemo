package com.example.kurslinemobileapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kurslinemobileapp.api.companyData.Category
import com.example.kurslinemobileapp.api.companyData.Region

class RegionAdapter (var regions: List<Region>) :
    RecyclerView.Adapter<RegionAdapter.RegionViewHolder>() {
    private var onItemClickListener: ((Region) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
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
    fun setOnItemClickListener(listener: (Region) -> Unit) {
        onItemClickListener = listener
    }

    inner class RegionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewName: TextView = itemView.findViewById(android.R.id.text1)

        fun bind(region: Region) {
            textViewName.text = region.regionName
            itemView.setOnClickListener {
                onItemClickListener?.invoke(region)
            }
        }
    }
}