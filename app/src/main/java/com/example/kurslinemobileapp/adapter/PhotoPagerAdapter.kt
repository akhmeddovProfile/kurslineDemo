package com.example.kurslinemobileapp.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.app.kurslinemobileapp.R
import com.example.kurslinemobileapp.model.uploadPhoto.SelectionPhotoShowOnViewPager

class PhotoPagerAdapter(var photoList: List<SelectionPhotoShowOnViewPager>,private val itemClickListener: OnItemClickListener// Add the delete listener
) :  RecyclerView.Adapter<PhotoPagerAdapter.PhotoViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageViewPager)
        val deleteimage:ImageView=itemView.findViewById(R.id.deleteimage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewpager_imagecontainer, parent, false)
        return PhotoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val imageBytes = android.util.Base64.decode(photoList[position].base64String, android.util.Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        holder.imageView.setImageBitmap(bitmap)

        holder.deleteimage.setOnClickListener {
            itemClickListener.onItemClick(position)
        }
        /*holder.imageView.setOnClickListener {
            itemClickListener.onItemClick(position)
        }*/

    }

    override fun getItemCount(): Int {
        return photoList.size
    }

}
