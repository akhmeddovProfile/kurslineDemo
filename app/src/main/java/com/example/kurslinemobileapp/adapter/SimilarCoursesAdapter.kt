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
import androidx.recyclerview.widget.RecyclerView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.api.announcement.getDetailAnnouncement.AnnouncementDetailModel
import com.example.kurslinemobileapp.api.announcement.getDetailAnnouncement.AnnouncementSimilarCourse
import com.example.kurslinemobileapp.api.announcement.getDetailAnnouncement.Comment
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.Announcemenet
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.Photo
import com.example.kurslinemobileapp.api.companyTeachers.companyProfile.Announcement
import com.squareup.picasso.Picasso

class SimilarCoursesAdapter(val items:ArrayList<AnnouncementSimilarCourse>, val context: Context):RecyclerView.Adapter<SimilarCoursesAdapter.SimilarCoursesAdapter>() {
    private var onItemClickListener: ((AnnouncementSimilarCourse) -> Unit)? = null
    fun setOnItemClickListener(listener: (AnnouncementSimilarCourse) -> Unit) {
        onItemClickListener = listener
    }
    inner class SimilarCoursesAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
        fun bind(elan: AnnouncementSimilarCourse) {
            itemView.setOnClickListener {
                onItemClickListener?.invoke(elan)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilarCoursesAdapter {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.product_item_row, parent, false)
        return SimilarCoursesAdapter(view)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: SimilarCoursesAdapter, position: Int) {
        val productRow = items[position]

        if (productRow.photos==null||productRow.photos.isEmpty()) {
            holder.productimage.setImageResource(R.drawable.splash)
        } else {
            var photoUrl: String? = null

            // Iterate through the photos list to find the first available URL
            for (photo in productRow.photos) {
                if (!photo.url.isNullOrEmpty()) {
                    photoUrl = photo.url
                    break
                }
            }
            if (photoUrl != null) {
                Picasso.get().load(photoUrl).into(holder.productimage)
            } else {
                // Set a default image if no valid photo URL is found
                holder.productimage.setImageResource(R.drawable.setpp)
            }
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

    }

    override fun getItemCount(): Int {
        return items.size
    }
}