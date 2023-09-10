package com.example.kurslinemobileapp.adapter

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.app.kurslinemobileapp.R
import com.example.kurslinemobileapp.model.uploadPhoto.PhotoUpload
import com.squareup.picasso.Picasso

class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(photo: PhotoUpload) {
        // Use Glide or Picasso to load and display the photo into an ImageView
        val imageView: ImageView = itemView.findViewById(R.id.imageViewPager)

        // Using Picasso:
        Picasso.get()
            .load(photo.uri)
            .into(imageView)
    }
}