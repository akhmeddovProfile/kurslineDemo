package com.example.kurslinemobileapp.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.model.AdsModel.AdModelItem
import com.squareup.picasso.Picasso

class ViewPagerImageAdapter(private val imageList: ArrayList<AdModelItem>, private val viewPager2: ViewPager2,private val context: Context) :
    RecyclerView.Adapter<ViewPagerImageAdapter.ImageViewHolder>() {

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageViewPager);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.viewpager_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = imageList[position].reklamPhoto
        // Load the image into the ImageView
        Picasso.get().load(imageUrl).into(holder.imageView)



        holder.imageView.setOnClickListener {
            val link = imageList[position].reklamLink
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            context.startActivity(intent)
        }

        if (position == imageList.size - 1) {
            viewPager2.post(runnable)
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    private val runnable = Runnable {
        imageList.addAll(imageList)
        notifyDataSetChanged()
    }

    private fun openReklamLink(context: Context, link: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))

        // Check if there's an app that can handle the intent
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
            intent.setPackage(intent.toString())
        } else {
            // Handle the case where no app can handle the link
            // You can show a message or take alternative actions here
            Toast.makeText(context, "No app can handle this link.", Toast.LENGTH_SHORT).show()
        }
    }
}