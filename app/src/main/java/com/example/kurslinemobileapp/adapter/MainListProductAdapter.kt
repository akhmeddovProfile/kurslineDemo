package com.example.kurslinemobileapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.Announcemenet
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.GetAllAnnouncement
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.Photo
import com.example.kurslinemobileapp.api.favorite.SendFavModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.product_item_row.view.*

class MainListProductAdapter(private val items: List<GetAllAnnouncement>,
                            // private val heartlistener: MainListProductAdapter.ListenerClickHeart,
                             private val favoriteItemClickListener: FavoriteItemClickListener,
                             //var favList:List<Int>,
                             private val context:Context,
                             private val favoriteItems: MutableList<SendFavModel> = mutableListOf() // Your list of favorite items
) :
    RecyclerView.Adapter<MainListProductAdapter.ProductRowHolder>() {


    private lateinit var sharedPreferences: SharedPreferences

    private var onItemClickListener: ((Announcemenet) -> Unit)? = null

    fun setOnItemClickListener(listener: (Announcemenet) -> Unit) {
        onItemClickListener = listener
    }

    interface ListenerClickHeart{
        fun onHeartItemCLick(heart: GetAllAnnouncement, liked:Boolean, position: Int)
    }

    interface FavoriteItemClickListener{
        fun onFavoriteItemClick(item: SendFavModel,isSelected:Boolean)
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
        val heartButton: ImageButton=itemView.findViewById(R.id.favorite_button)
        fun bind(elan: Announcemenet) {
            itemView.setOnClickListener {
                onItemClickListener?.invoke(elan)
            }
        }
        fun bindfav(item: SendFavModel) {
            // Update the UI of the heart drawable based on the selection status
            if (item.isSelected) {
                heartButton.setImageResource(R.drawable.favorite_for_product)
            } else {
                heartButton.setImageResource(R.drawable.favorite_border_for_product)
            }

            // Set click listener for the heart button
            heartButton.setOnClickListener {
                favoriteItemClickListener.onFavoriteItemClick(item,true)
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

        val productRow = items.get(0).announcemenets[position]
        val photoUrl = items.get(0).announcemenets[position].photos[0].url
        val url = "1"
        val photo = Photo(url)
        Picasso.get().load(photoUrl).transform(ResizeTransformation(300, 300)).into(holder.productimage)
        sharedPreferences =context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)


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
        val item = if (position < favoriteItems.size) {
            favoriteItems[position].copy(isSelected = true)
        } else {
            SendFavModel(0, 0, false)
        }
        /*val item = if (position < favoriteItems.size) favoriteItems[position] else SendFavModel(0, 0)*/
        holder.bindfav(item)
        if (item.isSelected) {
            holder.heartButton.setImageResource(R.drawable.favorite_for_product)
        } else {
            holder.heartButton.setImageResource(R.drawable.favorite_border_for_product)
        }
        holder.heartButton.setOnClickListener {
            // Toggle the favorite state of the item
            item.isSelected=!item.isSelected

            // Update the heart drawable based on the new favorite state
            if (item.isSelected) {
                holder.heartButton.setImageResource(R.drawable.favorite_for_product)
            } else {
                holder.heartButton.setImageResource(R.drawable.favorite_border_for_product)
            }


            // Call the interface method to handle the favorite action
            // Pass the item's ID, token, and user ID to the interface method
           item.productid= productRow.id
            favoriteItemClickListener.onFavoriteItemClick(SendFavModel(item.productid, item.userid,item.isSelected),item.isSelected)
        }
    }

    override fun getItemCount(): Int {
        return items.get(0).announcemenets.size
    }

    fun setFavoriteItems(items: List<SendFavModel>) {
        favoriteItems.clear()
        favoriteItems.addAll(items)
        notifyDataSetChanged()
    }
}