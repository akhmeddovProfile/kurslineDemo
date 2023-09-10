package com.example.kurslinemobileapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.kurslinemobileapp.R
import com.example.kurslinemobileapp.api.companyData.Category
import com.example.kurslinemobileapp.service.Room.category.CategoryEntity
import com.example.kurslinemobileapp.service.Room.category.CategoryWithSubCategory

class CategoryAdapter (var categories: List<CategoryWithSubCategory>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    private var onItemClickListener: ((CategoryWithSubCategory) -> Unit)? = null

    fun setOnItemClickListener(listener: (CategoryWithSubCategory) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    fun setChanged(categories: List<CategoryWithSubCategory>){
        this.categories = categories
        notifyDataSetChanged()
    }


   inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewName: TextView = itemView.findViewById(android.R.id.text1)

        @SuppressLint("ResourceAsColor")
        fun bind(category: CategoryWithSubCategory) {
            textViewName.setTextColor(R.color.black)
            textViewName.text = category.category.categoryName
            itemView.setOnClickListener {
                onItemClickListener?.invoke(category)
            }
        }
    }
}