package com.example.kurslinemobileapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.Announcemenet
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.GetAllAnnouncement
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.Photo
import com.example.kurslinemobileapp.api.companyTeachers.companyTeacherRow.CompanyTeacherModelItem
import com.example.kurslinemobileapp.service.Constant
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.product_item_row.view.*
import java.util.*
import kotlin.collections.ArrayList

class VIPAdapter(private var items: List<Announcemenet>,
                             private val favoriteItemClickListener: VIPFavoriteItemClickListener,
                             private val context:Context
) :
    RecyclerView.Adapter<VIPAdapter.ProductRowHolder>() {
    var fullList :kotlin.collections.List<Announcemenet>
    var newList = arrayListOf<Announcemenet>()

    private var onItemClickListener: ((Announcemenet) -> Unit)? = null

    init {
        fullList = items
    }

    fun setOnItemClickListener(listener: (Announcemenet) -> Unit) {
        onItemClickListener = listener
    }
    private lateinit var sharedPreferences: SharedPreferences

    interface VIPFavoriteItemClickListener{
        fun VIPonFavoriteItemClick(id: Int,position: Int)
    }
    inner class ProductRowHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val isonlinebg : RelativeLayout = itemView.findViewById(R.id.relativeForCourseMode)
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

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n", "CommitPrefEdits")
    override fun onBindViewHolder(holder: ProductRowHolder, position: Int) {
        var addedToFav = false
        val productRow = items[position]

        if (productRow.photos.isNotEmpty()) {
            val photoUrl = productRow.photos[0].url
            Picasso.get().load(photoUrl).into(holder.productimage)
        } else {
            // Handle the case when the list is empty
            // You can set a placeholder image or display a message here
            Picasso.get().load(R.drawable.yenielan2).into(holder.productimage)
        }



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
        sharedPreferences=context.getSharedPreferences(Constant.sharedkeyname, Context.MODE_PRIVATE)

        // Set the heart button background color based on the favorite status
        val userId = sharedPreferences.getInt("userID", 0)
        if (productRow.isFavorite==true){
            holder.heartButton.setImageResource(R.drawable.favorite_for_product)
        } else {
            holder.heartButton.setImageResource(R.drawable.favorite_border_for_product)
        }
        // sharedPreferences.edit().putBoolean("isFavoriteItemForDetail_${position}",productRow.isFavorite).apply()

        holder.heartButton.setOnClickListener {
            if (userId!=0){
                favoriteItemClickListener.VIPonFavoriteItemClick(productRow.id,position)
            }
            else{
                Toast.makeText(context, "Please log in@", Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun LikedItemsforVip(position: Int,isFavorite:Boolean){
        items[position].isFavorite = isFavorite
        notifyItemChanged(position)

    }

    fun notifySetChanged(productList: MutableList<Announcemenet>){
        items = productList
        notifyDataSetChanged()
    }



    fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                newList.clear()
                if (charSearch.isEmpty() ) {
                    newList.addAll(fullList)
                } else {
//                    val resultList = ArrayList()
                    for (row in fullList) {
                        if (row.announcementName.lowercase(Locale.ROOT)
                                .contains(charSearch.lowercase(Locale.ROOT))
                        ) {
                            newList.add(row)
                        }
                    }
//                    countryFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = newList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                var newFullList :ArrayList<Announcemenet>
                newFullList = arrayListOf()
                newFullList.addAll(newList)
                items = newFullList
                notifyDataSetChanged()
            }

        }
    }


}
