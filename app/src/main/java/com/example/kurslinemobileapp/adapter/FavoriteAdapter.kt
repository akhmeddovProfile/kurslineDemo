package com.example.kurslinemobileapp.adapter

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
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.app.kurslinemobileapp.R
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.Photo
import com.example.kurslinemobileapp.api.favorite.favoriteGet.FavoriteGetModelItem
import com.squareup.picasso.Picasso

class FavoriteAdapter(
    private var items: List<FavoriteGetModelItem>,
    private val deleteItem: DeleteItemFromFavorite,
):RecyclerView.Adapter<FavoriteAdapter.ItemView>() {

    private var onItemClickListener: ((FavoriteGetModelItem) -> Unit)? = null

    fun setOnItemClickListener(listener: (FavoriteGetModelItem) -> Unit) {
        onItemClickListener = listener
    }

    interface DeleteItemFromFavorite{
        fun deletefavoriteOnItemClick(id:Int,position: Int)
    }
    inner class ItemView(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val isonlinebg : RelativeLayout = itemView.findViewById(R.id.favoriteRelativeForCourseMode)
        val modeView: TextView = itemView.findViewById(R.id.favoriteModeforproduct)
        val statusView: TextView = itemView.findViewById(R.id.favoritestatusforproduct)
        val imageVIPView: ImageView = itemView.findViewById(R.id.favorite_vip_product)
        val productimage: ImageView = itemView.findViewById(R.id.favorite_productImage)
        val producttitle: TextView = itemView.findViewById(R.id.favoriteProductTitle)
        val productOwnerName: TextView = itemView.findViewById(R.id.favoriteProductOwnerName)
        val productDescription: TextView =
            itemView.findViewById(R.id.favoriteProductDescriptionIntheMainScreen)
        val deleteButton: ImageButton = itemView.findViewById(R.id.favorite_button2)
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

        @RequiresApi(Build.VERSION_CODES.M)
        override fun onBindViewHolder(holder: ItemView, position: Int) {

            val productRow = items[position]
            val photoUrl = productRow.photos.get(0).url
            val url = "1"
            val photo = Photo(url)
            Picasso.get().load(photoUrl).transform(ResizeTransformation(1000, 800)).into(holder.productimage)
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
            if (productRow.isVIP == true) {
                holder.imageVIPView.visibility = View.VISIBLE
            } else {
                holder.imageVIPView.visibility = View.GONE
            }

            holder.bind(productRow)


            if(items.isNotEmpty()){
                holder.deleteButton.setBackgroundResource(R.drawable.favorite_for_product)
            }else{
                holder.deleteButton.setBackgroundResource(R.drawable.favorite_border_for_product)
            }



            holder.deleteButton.setOnClickListener {
                deleteItem.deletefavoriteOnItemClick(productRow.id,position)
                println("Deleted")
            }

        }

    fun deleteItems(items:List<FavoriteGetModelItem>,position:Int){
        this.items=items
        notifyItemChanged(position)
    }

    fun notifySetChanged(productList: MutableList<FavoriteGetModelItem>){
        items = productList
        notifyDataSetChanged()
    }
       override fun getItemCount(): Int {
            return items.size
        }

}