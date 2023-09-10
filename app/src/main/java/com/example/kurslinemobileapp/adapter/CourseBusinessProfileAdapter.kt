package com.example.kurslinemobileapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
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
        val isonlinebg : RelativeLayout = itemView.findViewById(R.id.relativeForCourseMode)
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

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val productRow = products[position]
        if (productRow.photos.isNotEmpty()) {
            val photoUrl = productRow.photos[0].url
            Picasso.get().load(photoUrl).into(holder.productimage)
        } else {
            // Handle the case when the list is empty
            // You can set a placeholder image or display a message here
            Picasso.get().load(R.drawable.yenielan2).into(holder.productimage)
        }

        val context: Context = holder.itemView.context
        holder.modeView.text = productRow.isOnline
        if(productRow.isOnline == "Online"){
            holder.isonlinebg.setBackgroundResource(R.drawable.isonline_bg)
            holder.modeView.setTextColor(context.getColor(R.color.white))
        }else{
            holder.isonlinebg.setBackgroundResource(R.drawable.status_view)
            holder.modeView.setTextColor(context.getColor(R.color.colorForCourseIntheMainScreen))
        }
        holder.statusView.text = productRow.isStatus
        //holder.imageVIPView.setImageResource(productRow.vipIcon)
        holder.producttitle.text = productRow.announcementName
        holder.productOwnerName.text = productRow.companyName
        holder.productDescription.text = productRow.announcementDesc
        holder.heartButton.visibility=View.GONE
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