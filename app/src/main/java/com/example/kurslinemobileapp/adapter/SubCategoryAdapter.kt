package com.example.kurslinemobileapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.api.companyData.Category
import com.example.kurslinemobileapp.api.companyData.SubCategory
import com.example.kurslinemobileapp.service.Room.category.SubCategoryEntity

class SubCategoryAdapter (var subCategories: List<SubCategoryEntity>) :
    RecyclerView.Adapter<SubCategoryAdapter.CategoryViewHolder>() {
    private var onItemClickListener: ((SubCategoryEntity) -> Unit)? = null

    fun setOnItemClickListener(listener: (SubCategoryEntity) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val subCategory = subCategories[position]
        holder.bind(subCategory)
    }

    override fun getItemCount(): Int {
        return subCategories.size
    }

    fun setChanged(categories: List<SubCategoryEntity>){
        this.subCategories = categories
        notifyDataSetChanged()
    }


    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewName: TextView = itemView.findViewById(android.R.id.text1)

        @SuppressLint("ResourceAsColor")
        fun bind(subCategory: SubCategoryEntity) {
            textViewName.setTextColor(R.color.black)
            textViewName.text = subCategory.subCategoryName
            itemView.setOnClickListener {
                onItemClickListener?.invoke(subCategory)
            }
        }
    }
}