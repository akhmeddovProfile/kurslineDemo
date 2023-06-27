package com.example.kurslinemobileapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.Announcemenet
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.GetAllAnnouncement
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.Photo
import com.example.kurslinemobileapp.api.favorite.GetFavoritesItemModel
import com.example.kurslinemobileapp.api.favorite.favoriteGet.FavoriteGetModelItem
import com.squareup.picasso.Picasso

class FavoriteAdapter(private val items: ArrayList<FavoriteGetModelItem>):RecyclerView.Adapter<FavoriteAdapter.ItemView>() {

    private var onItemClickListener: ((FavoriteGetModelItem) -> Unit)? = null

    fun setOnItemClickListener(listener: (FavoriteGetModelItem) -> Unit) {
        onItemClickListener = listener
    }

    inner class ItemView(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val modeView: TextView = itemView.findViewById(R.id.favoriteModeforproduct)
        val statusView: TextView = itemView.findViewById(R.id.favoritestatusforproduct)
        val imageVIPView: ImageView = itemView.findViewById(R.id.favorite_vip_product)
        val productimage: ImageView = itemView.findViewById(R.id.favorite_productImage)
        val producttitle: TextView = itemView.findViewById(R.id.favoriteProductTitle)
        val productOwnerName: TextView = itemView.findViewById(R.id.favoriteProductOwnerName)
        val productDescription: TextView =
            itemView.findViewById(R.id.favoriteProductDescriptionIntheMainScreen)
        val heartButton: ImageButton = itemView.findViewById(R.id.favorite_button2)
        fun bind(elan: FavoriteGetModelItem) {
            itemView.setOnClickListener {
                onItemClickListener?.invoke(elan)
            }
        }
        init {
            // Set click listener for the itemView
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.invoke(items[position])
                }
            }
        }
    }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemView {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.favorite_product_row, parent, false)
            return ItemView(view)
        }

        override fun onBindViewHolder(holder: ItemView, position: Int) {

            val productRow = items[position]
            val photoUrl = items.get(0).photos[position].url
            val url = "1"
            val photo = Photo(url)
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

            holder.heartButton.setOnClickListener {

            }
        }

       override fun getItemCount(): Int {
            return items.size
        }

}