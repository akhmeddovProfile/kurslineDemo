package com.example.kurslinemobileapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.app.kurslinemobileapp.R
import com.squareup.picasso.Picasso

class ProductDetailImageAdapter(private val photos: List<com.example.kurslinemobileapp.api.announcement.getDetailAnnouncement.Photo>) : RecyclerView.Adapter<ProductDetailImageAdapter.PhotoViewHolder>() {

    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.imagecontainer_post_detail, parent, false)
        return PhotoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = photos[position]
        Picasso.get()
            .load(photo.url).into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return photos.size
    }
}