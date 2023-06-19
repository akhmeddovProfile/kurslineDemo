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
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.Announcemenet
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.GetAllAnnouncement
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.Photo
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.product_item_row.view.*

class MainListProductAdapter(private val items: List<GetAllAnnouncement>,
                             private val heartlistener: MainListProductAdapter.ListenerClickHeart,
                             var favList:List<Int>
) :
    RecyclerView.Adapter<MainListProductAdapter.ProductRowHolder>() {
    private var onItemClickListener: ((Announcemenet) -> Unit)? = null

    fun setOnItemClickListener(listener: (Announcemenet) -> Unit) {
        onItemClickListener = listener
    }
    interface ListenerClickHeart{
        fun onHeartItemCLick(heart: GetAllAnnouncement, liked:Boolean, position: Int)
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductRowHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.product_item_row, parent, false)
        return ProductRowHolder(view)
    }

    @SuppressLint("SetTextI18n")

    override fun onBindViewHolder(holder: ProductRowHolder, position: Int) {
        var addedToFav = false
        val productRow = items.get(0).announcemenets[position]
        val photoUrl = items.get(0).announcemenets[position].photos[0].url
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

        //FavBtn
        //position < items.size && position < items[position].announcemenets.size && favList.contains(items[position].announcemenets[position].id)
        //favList.contains(items[position].announcemenets.get(position).id)
        if(position < items.size && position < items[position].announcemenets.size && favList.contains(items[position].announcemenets[position].id)){
        holder.heartButton.setBackgroundResource(R.drawable.favorite_for_product)
            addedToFav=true
            println("addedToFav: $addedToFav")
            return
        }
        else{
            holder.heartButton.setBackgroundResource(R.drawable.favorite_border_for_product)
            println("addedToFav: $addedToFav")

        }

        holder.heartButton.setOnClickListener {
            val currentPosition = holder.adapterPosition
            if (currentPosition != RecyclerView.NO_POSITION && currentPosition < items.size) {
                if (addedToFav) {
                    heartlistener.onHeartItemCLick(items[currentPosition], addedToFav, currentPosition)
                    addedToFav = false
                    holder.heartButton.setBackgroundResource(R.drawable.favorite_border_for_product)
                } else {
                    heartlistener.onHeartItemCLick(items[currentPosition], addedToFav, currentPosition)
                    addedToFav = true
                    holder.heartButton.setBackgroundResource(R.drawable.favorite_for_product)
                }

            }

        }
    }


    override fun getItemCount(): Int {
        return items.get(0).announcemenets.size
    }
}