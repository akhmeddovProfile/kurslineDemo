package com.example.kurslinemobileapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.api.companyData.Category
import com.example.kurslinemobileapp.api.companyTeachers.companyTeacherRow.CompanyTeacherModelItem
import com.example.kurslinemobileapp.service.Room.category.CategoryEntity
import com.example.kurslinemobileapp.service.Room.category.CategoryWithSubCategory
import com.squareup.picasso.Picasso

class CourseFilterAdapter (var categories: List<CompanyTeacherModelItem>) :
    RecyclerView.Adapter<CourseFilterAdapter.CategoryViewHolder>() {
    private var onItemClickListener: ((CompanyTeacherModelItem) -> Unit)? = null

    fun setOnItemClickListener(listener: (CompanyTeacherModelItem) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bottom_sheet_dialog_courses, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    fun setChanged(categories: List<CompanyTeacherModelItem>){
        this.categories = categories
        notifyDataSetChanged()
    }


    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewName: TextView = itemView.findViewById(R.id.companyTeacherCname)
        private val viewImage : ImageView = itemView.findViewById(R.id.companyTeacherTabImage)
        @SuppressLint("ResourceAsColor")
        fun bind(category: CompanyTeacherModelItem) {
            textViewName.setTextColor(R.color.black)
            textViewName.text = category.companyCategoryName
            if ( category.companyImage == null) {
                // If the companyImage is null, set a default image from a local drawable resource
             viewImage.setImageResource(R.drawable.setpp)
            } else {
                // If the companyImage is not null, load the image using Picasso library
                Picasso.get()
                    .load(category.companyImage)
                    .transform(ResizeTransformation(300, 300))
                    .into(viewImage)
            }

            itemView.setOnClickListener {
                onItemClickListener?.invoke(category)
            }
        }
    }
}