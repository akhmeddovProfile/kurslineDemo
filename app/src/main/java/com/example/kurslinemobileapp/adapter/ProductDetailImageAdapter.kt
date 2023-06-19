package com.example.kurslinemobileapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.Photo
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.imagecontainer_post_detail.view.*

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
            .load(photo.url).transform(ResizeTransformation(1000, 800))
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return photos.size
    }
}