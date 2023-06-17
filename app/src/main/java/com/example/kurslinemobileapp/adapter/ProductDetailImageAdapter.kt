package com.example.kurslinemobileapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.kurslinemobileapp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.imagecontainer_post_detail.view.*

class ProductDetailImageAdapter(private val imageList: ArrayList<String>, private val viewPager2: ViewPager2) :
    RecyclerView.Adapter<ProductDetailImageAdapter.ImageViewHolder>() {

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.imageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.imagecontainer_post_detail, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        Picasso.get().load(imageList[position]).into(holder.imageView)

        //holder.imageView.setImageResource(imageList[position])
        if (position == imageList.size-1){
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
}