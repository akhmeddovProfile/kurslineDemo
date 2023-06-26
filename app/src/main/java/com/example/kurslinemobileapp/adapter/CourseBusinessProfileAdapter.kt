package com.example.kurslinemobileapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.api.companyTeachers.companyProfile.Announcement
import com.squareup.picasso.Picasso

class CourseBusinessProfileAdapter (var products: List<Announcement>) :
    RecyclerView.Adapter<CourseBusinessProfileAdapter.CategoryViewHolder>() {
    private var onItemClickListener: ((Announcement) -> Unit)? = null

    fun setOnItemClickListener(listener: (Announcement) -> Unit) {
        onItemClickListener = listener
    }
    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val modeView: TextView = itemView.findViewById(R.id.modeforproduct)
        val statusView: TextView = itemView.findViewById(R.id.statusforproduct)
        val imageVIPView: ImageView = itemView.findViewById(R.id.vip_product)
        val productimage: ImageView = itemView.findViewById(R.id.productImage)
        val producttitle: TextView = itemView.findViewById(R.id.productTitle)
        val productOwnerName: TextView = itemView.findViewById(R.id.productOwnerName)
        val productDescription: TextView =
            itemView.findViewById(R.id.productDescriptionIntheMainScreen)
        val heartButton: ImageButton =itemView.findViewById(R.id.favorite_button)
        fun bind(elan: Announcement) {
            itemView.setOnClickListener {
                onItemClickListener?.invoke(elan)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item_row, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val productRow = products[position]
        val photoUrl = products[position].photos[0].url
        Picasso.get().load(photoUrl).transform(ResizeTransformation(300, 300)).into(holder.productimage)


        holder.modeView.text = productRow.isOnline
        holder.statusView.text = productRow.isStatus
        //holder.imageVIPView.setImageResource(productRow.vipIcon)
        holder.producttitle.text = productRow.announcementName
        holder.productOwnerName.text = productRow.companyName
        holder.productDescription.text = productRow.announcementDesc
        if (productRow.isVIP == true) {
            holder.imageVIPView.visibility = View.VISIBLE
        } else {
            holder.imageVIPView.visibility = View.GONE
        }
        holder.bind(productRow)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    fun setChanged(product: List<Announcement>){
        this.products = product
        notifyDataSetChanged()
    }
}