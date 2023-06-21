package com.example.kurslinemobileapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.Photo
import com.example.kurslinemobileapp.api.companyTeachers.companyTeacherRow.CompanyTeacherModelItem
import com.example.kurslinemobileapp.view.courseFmAc.CourseBusinessProfile
import com.squareup.picasso.Picasso

class CompanyTeacherAdapter (private val items: ArrayList<CompanyTeacherModelItem>) :
    RecyclerView.Adapter<CompanyTeacherAdapter.CompanyTeacherHolder>() {

    inner class CompanyTeacherHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val companyName: TextView = itemView.findViewById(R.id.companyTeacherCname)
        val companyCategory: TextView = itemView.findViewById(R.id.companyTeacherCategory)
        val companyImage: ImageView = itemView.findViewById(R.id.companyTeacherTabImage)
        val companyPhone: ImageView = itemView.findViewById(R.id.phoneCompanyTeacher)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            val context = itemView.context
            val companyId = items[position].companyId

            // Navigate to the desired activity and pass the companyId as an extra
            val intent = Intent(context, CourseBusinessProfile::class.java)
            intent.putExtra("companyId", companyId)
            context.startActivity(intent)
        }

        fun bind(companyItem: CompanyTeacherModelItem) {
            // Handle any additional actions or bindings for the item here if needed
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyTeacherHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.companyteacherrow, parent, false)
        return CompanyTeacherHolder(view)
    }

    override fun onBindViewHolder(holder: CompanyTeacherHolder, position: Int) {
        val companyItem = items[position]
        val url = "1"
        val photo = Photo(url)
        if (companyItem.companyImage == null) {
            // If the companyImage is null, set a default image from a local drawable resource
           holder.companyImage.setImageResource(R.drawable.setpp)
        } else {
            // If the companyImage is not null, load the image using Picasso library
            Picasso.get()
                .load(companyItem.companyImage)
                .transform(ResizeTransformation(300, 300))
                .into(holder.companyImage)
        }

        holder.companyName.text = companyItem.companyName
        holder.companyCategory.text = companyItem.companyCategoryName
        holder.bind(companyItem)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}