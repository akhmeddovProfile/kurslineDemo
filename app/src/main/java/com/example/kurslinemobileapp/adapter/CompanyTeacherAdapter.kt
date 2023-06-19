package com.example.kurslinemobileapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.GetAllAnnouncement
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.Photo
import com.example.kurslinemobileapp.api.companyTeachers.CompanyTeacherModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_account.view.*
import kotlinx.android.synthetic.main.product_item_row.view.*

class CompanyTeacherAdapter (private val items: ArrayList<CompanyTeacherModel>) :
    RecyclerView.Adapter<CompanyTeacherAdapter.CompanyTeacherHolder>() {

    inner class CompanyTeacherHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val companyName: TextView = itemView.findViewById(R.id.companyTeacherCname)
        val companyCategory: TextView = itemView.findViewById(R.id.companyTeacherCategory)
        val companyImage: ImageView = itemView.findViewById(R.id.companyTeacherTabImage)
        val companyPhone : ImageView = itemView.findViewById(R.id.phoneCompanyTeacher)
        fun bind(elan: CompanyTeacherModel) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyTeacherHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.companyteacherrow, parent, false)
        return CompanyTeacherHolder(view)
    }

    override fun onBindViewHolder(holder: CompanyTeacherHolder, position: Int) {
        val productRow = items.get(0)
        val url = "1"
        val photo = Photo(url)
        if (productRow[position].companyImage == null) {
            // If the companyImage is null, set a default image from a local drawable resource
           holder.companyImage.setImageResource(R.drawable.setpp)
        } else {
            // If the companyImage is not null, load the image using Picasso library
            Picasso.get()
                .load(productRow[position].companyImage)
                .transform(ResizeTransformation(300, 300))
                .into(holder.companyImage)
        }

        holder.companyName.text = productRow.get(position).companyName
        holder.companyCategory.text = productRow.get(position).companyCategoryName
        holder.bind(productRow)
    }

    override fun getItemCount(): Int {
        return items.get(0).size
    }
}