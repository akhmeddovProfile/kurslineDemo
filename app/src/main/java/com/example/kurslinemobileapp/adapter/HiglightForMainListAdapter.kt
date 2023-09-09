package com.example.kurslinemobileapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.model.mainpage.Highlight
import com.squareup.picasso.Picasso

class HiglightForMainListAdapter(private val items: List<Highlight>,  private val clickListener: OnHighlightItemClickListener) :
    RecyclerView.Adapter<HiglightForMainListAdapter.ImageRowViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageRowViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.highlight_item_row, parent, false)
        return ImageRowViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageRowViewHolder, position: Int) {
        val imageRow = items[position]
        Picasso.get()
            .load(imageRow.highlightImage)
            .transform(ResizeTransformation(300, 300)) // Adjust the size as per your requirements
            .into(holder.imageView)
        holder.textView.text = imageRow.highlightName
    }

    override fun getItemCount(): Int {
        return items.size
    }


    inner class ImageRowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageText)
        val textView: TextView = itemView.findViewById(R.id.textImage)
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clickedItem = items[position]
                    clickListener.onItemClick(clickedItem)
                }
            }
        }
    }
    interface OnHighlightItemClickListener {
        fun onItemClick(item: Highlight)
    }

}