package com.example.kurslinemobileapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.model.mainpage.Product

class MainListProductAdapter(private val items: List<Product>) :
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
        holder.statusView.text = productRow.status
        holder.imageVIPView.setImageResource(productRow.vipIcon)
        holder.productimage.setImageResource(productRow.productImage)
        holder.producttitle.text = productRow.productTitle
        holder.productOwnerName.text = productRow.ownerName
        holder.productDescription.text = productRow.productDescription
    }

    override fun getItemCount(): Int {
        return items.size
    }
}