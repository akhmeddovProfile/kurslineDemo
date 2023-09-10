package com.example.kurslinemobileapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.kurslinemobileapp.R
import com.example.kurslinemobileapp.service.Room.status.StatusEntity

class StatusAdapter (var status: List<StatusEntity>) :
    RecyclerView.Adapter<StatusAdapter.StatusViewHolder>() {
    private var onItemClickListener: ((StatusEntity) -> Unit)? = null

    fun setOnItemClickListener(listener: (StatusEntity) -> Unit) {
        onItemClickListener = listener
    }
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

    fun setChanged(status: List<StatusEntity>){
        this.status = status
        notifyDataSetChanged()
    }

    inner class StatusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewName: TextView = itemView.findViewById(android.R.id.text1)

        @SuppressLint("ResourceAsColor")
        fun bind(status: StatusEntity) {
            textViewName.setTextColor(R.color.black)
            textViewName.text = status.statusName
            itemView.setOnClickListener {
                onItemClickListener?.invoke(status)
            }
        }
    }
}