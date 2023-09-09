package com.example.kurslinemobileapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.api.announcement.higlightProduct.HiglightProductModel
import com.example.kurslinemobileapp.api.announcement.higlightProduct.HiglightProductModelItem
import com.example.kurslinemobileapp.api.getUserCmpDatas.companyAnnouncement.CompanyTransactionAnnouncementItem
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class HiglighProducAdapter (var items: List<HiglightProductModelItem>, private val context: Context) :
    RecyclerView.Adapter<HiglighProducAdapter.ProductRowHolder>() {

    var fullList :kotlin.collections.List<HiglightProductModelItem>
    var newList = arrayListOf<HiglightProductModelItem>()
    init {
        fullList = items
    }
    private var onItemClickListener: ((HiglightProductModelItem) -> Unit)? = null

    fun setOnItemClickListener(listener: (HiglightProductModelItem) -> Unit) {
        onItemClickListener = listener
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
        val heartButton: ImageButton =itemView.findViewById(R.id.favorite_button)
        fun bind(elan: HiglightProductModelItem) {
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
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProductRowHolder, position: Int) {
        val productRow =  items[position]
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
        holder.heartButton.visibility= View.INVISIBLE
        holder.bind(productRow)

    }


    override fun getItemCount(): Int {
        return items.size
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
                var newFullList :ArrayList<HiglightProductModelItem>
                newFullList = arrayListOf()
                newFullList.addAll(newList)
                items = newFullList
                notifyDataSetChanged()
            }

        }
    }
}