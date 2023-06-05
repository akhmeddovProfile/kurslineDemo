package com.example.kurslinemobileapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.Announcemenet
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.GetAllAnnouncement
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.Photo
import com.example.kurslinemobileapp.model.mainpage.Product
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.product_item_row.view.*

class MainListProductAdapter(private val items: ArrayList<GetAllAnnouncement>) :
    RecyclerView.Adapter<MainListProductAdapter.ProductRowHolder>() {

    inner class ProductRowHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val statusView: TextView = itemView.findViewById(R.id.statusforproduct)
        val imageVIPView: ImageView = itemView.findViewById(R.id.vip_product)
        val productimage: ImageView = itemView.findViewById(R.id.productImage)
        val producttitle: TextView = itemView.findViewById(R.id.productTitle)
        val productOwnerName: TextView = itemView.findViewById(R.id.productOwnerName)
        val productDescription: TextView =
            itemView.findViewById(R.id.productDescriptionIntheMainScreen)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductRowHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.product_item_row, parent, false)
        return ProductRowHolder(view)
    }

    override fun onBindViewHolder(holder: ProductRowHolder, position: Int) {
        val productRow = items[position]
        val url = "1"
         val photo = Photo(url)
        Picasso.get().load(photo.url).into(holder.itemView.productImage)
        holder.statusView.text = productRow.announcemenets.get(position).isRejim
        //holder.imageVIPView.setImageResource(productRow.vipIcon)
        holder.producttitle.text = productRow.announcemenets.get(position).announcemementName
        holder.productOwnerName.text = productRow.announcemenets.get(position).companyName
        holder.productDescription.text = productRow.announcemenets.get(position).announcemementDesc
    }

    override fun getItemCount(): Int {
        return items.size
    }
}