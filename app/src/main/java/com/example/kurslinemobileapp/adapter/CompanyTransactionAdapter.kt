package com.example.kurslinemobileapp.adapter

import android.annotation.SuppressLint
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.Photo
import com.example.kurslinemobileapp.api.getUserCmpDatas.companyAnnouncement.CompanyTransactionAnnouncement
import com.example.kurslinemobileapp.api.getUserCmpDatas.companyAnnouncement.CompanyTransactionAnnouncementItem
import com.squareup.picasso.Picasso

class CompanyTransactionAdapter (private val items: List<CompanyTransactionAnnouncementItem>) :
    RecyclerView.Adapter<CompanyTransactionAdapter.ProductRowHolder>() {
    private var onItemClickListener: ((CompanyTransactionAnnouncementItem) -> Unit)? = null

    fun setOnItemClickListener(listener: (CompanyTransactionAnnouncementItem) -> Unit) {
        onItemClickListener = listener
    }
    inner class ProductRowHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val modeView: TextView = itemView.findViewById(R.id.modeforproduct)
        val statusView: TextView = itemView.findViewById(R.id.statusforproduct)
        val imageVIPView: ImageView = itemView.findViewById(R.id.vip_product)
        val productimage: ImageView = itemView.findViewById(R.id.productImage)
        val producttitle: TextView = itemView.findViewById(R.id.productTitle)
        val productOwnerName: TextView = itemView.findViewById(R.id.productOwnerName)
        val productDescription: TextView =
            itemView.findViewById(R.id.productDescriptionIntheMainScreen)
        val heartButton: ImageButton =itemView.findViewById(R.id.favorite_button)
        fun bind(elan: CompanyTransactionAnnouncementItem) {
            itemView.setOnClickListener {
                onItemClickListener?.invoke(elan)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductRowHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.product_item_row, parent, false)
        return ProductRowHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProductRowHolder, position: Int) {
        val productRow =  items[position]
        val photoUrl = productRow.photos.get(0).url
        val url = "1"
        Picasso.get().load(photoUrl).transform(ResizeTransformation(1000, 800)).into(holder.productimage)
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
        holder.heartButton.visibility=View.INVISIBLE
        holder.bind(productRow)

    }


    override fun getItemCount(): Int {
        return items.size
    }
}