package com.example.kurslinemobileapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kurslinemobileapp.api.companyData.IsOnline
import com.example.kurslinemobileapp.api.companyData.Statuse

class StatusAdapter (var status: List<Statuse>) :
    RecyclerView.Adapter<StatusAdapter.StatusViewHolder>() {
    private var onItemClickListener: ((Statuse) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return StatusViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        val status = status[position]
        holder.bind(status)
    }

    override fun getItemCount(): Int {
        return status.size
    }

    fun setChanged(status: List<Statuse>){
        this.status = status
        notifyDataSetChanged()
    }
    fun setOnItemClickListener(listener: (Statuse) -> Unit) {
        onItemClickListener = listener
    }

    inner class StatusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewName: TextView = itemView.findViewById(android.R.id.text1)

        fun bind(status: Statuse) {
            textViewName.text = status.statusName
            itemView.setOnClickListener {
                onItemClickListener?.invoke(status)
            }
        }
    }
}