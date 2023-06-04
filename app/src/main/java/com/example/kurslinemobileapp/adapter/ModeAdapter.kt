package com.example.kurslinemobileapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kurslinemobileapp.api.companyData.IsOnline
import com.example.kurslinemobileapp.api.companyData.Region


class ModeAdapter (var modes: List<IsOnline>) :
    RecyclerView.Adapter<ModeAdapter.ModeViewHolder>() {
    private var onItemClickListener: ((IsOnline) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return ModeViewHolder(view)
    }

    override fun onBindViewHolder(holder: ModeViewHolder, position: Int) {
        val mode = modes[position]
        holder.bind(mode)
    }

    override fun getItemCount(): Int {
        return modes.size
    }

    fun setChanged(modes: List<IsOnline>){
        this.modes = modes
        notifyDataSetChanged()
    }
    fun setOnItemClickListener(listener: (IsOnline) -> Unit) {
        onItemClickListener = listener
    }

    inner class ModeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewName: TextView = itemView.findViewById(android.R.id.text1)

        fun bind(mode: IsOnline) {
            textViewName.text = mode.isOnlineName
            itemView.setOnClickListener {
                onItemClickListener?.invoke(mode)
            }
        }
    }
}