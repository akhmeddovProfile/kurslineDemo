package com.example.kurslinemobileapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.model.uploadPhoto.ViewPagerFormData
import com.squareup.picasso.Picasso

class PhotoPagerAdapterForFormData (var photoList: List<ViewPagerFormData>) : RecyclerView.Adapter<PhotoPagerAdapterForFormData.PhotoViewHolder>() {

    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageViewPager)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewpager_imagecontainer, parent, false)
        return PhotoViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: PhotoPagerAdapterForFormData.PhotoViewHolder, position: Int) {
        val photo = photoList[position]

        if (photo.compressedBitmap != null) {
            holder.imageView.setImageBitmap(photo.compressedBitmap)
        } else if (photo.imagePath != null) {
            // Load the image from the imagePath
            loadImageFromPath(holder.imageView, photo.imagePath.toString() )
        } else {
            // Set a default image or handle the case where both imagePath and compressedBitmap are null.
            holder.imageView.setImageResource(R.drawable.kurslinelogo)
        }
    }


    override fun getItemCount(): Int {
        return photoList.size
    }
    private fun loadImageFromPath(imageView: ImageView, imagePath: String) {
        // Implement your logic to load and display an image from the imagePath.
        // You can use a library like Picasso or Glide for efficient image loading.
        // For example, using Picasso:
         Picasso.get().load(imagePath).into(imageView)
    }

}