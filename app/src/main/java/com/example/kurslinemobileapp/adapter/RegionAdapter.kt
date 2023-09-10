package com.example.kurslinemobileapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.kurslinemobileapp.R
import com.example.kurslinemobileapp.service.Room.region.RegionEntity

class RegionAdapter (var regions: List<RegionEntity>) :
    RecyclerView.Adapter<RegionAdapter.RegionViewHolder>() {
    private var onItemClickListener: ((RegionEntity) -> Unit)? = null

    fun setOnItemClickListener(listener: (RegionEntity) -> Unit) {
        onItemClickListener = listener
    }
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
    fun setChanged(regions: List<RegionEntity>){
        this.regions = regions
        notifyDataSetChanged()
    }
    inner class RegionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewName: TextView = itemView.findViewById(android.R.id.text1)

        @SuppressLint("ResourceAsColor")
        fun bind(region: RegionEntity) {
            textViewName.setTextColor(R.color.black)
            textViewName.text = region.regionName
            itemView.setOnClickListener {
                onItemClickListener?.invoke(region)
            }
        }
    }
}