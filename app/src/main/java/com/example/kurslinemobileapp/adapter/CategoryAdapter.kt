package com.example.kurslinemobileapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kurslinemobileapp.api.companyData.Category

class CategoryAdapter (var categories: List<Category>,val listener:ListenerItemClick) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    private var onItemClickListener: ((Category) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category)

        holder.itemView.setOnClickListener {
            listener.onCategoryItemCLick(categories.get(position),position)
        }

    }

    override fun getItemCount(): Int {
        return categories.size
    }

    fun setChanged(categories: List<Category>){
        this.categories = categories
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: (Category) -> Unit) {
        onItemClickListener = listener
    }

    //Emin
    interface ListenerItemClick{
        fun onCategoryItemCLick(category: Category,position: Int)
    }

   inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewName: TextView = itemView.findViewById(android.R.id.text1)

        fun bind(category: Category) {
            textViewName.text = category.categoryName
            itemView.setOnClickListener {
                onItemClickListener?.invoke(category)
            }
        }
    }
}