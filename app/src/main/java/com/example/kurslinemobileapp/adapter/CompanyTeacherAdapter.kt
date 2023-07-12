package com.example.kurslinemobileapp.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.Photo
import com.example.kurslinemobileapp.api.companyTeachers.companyTeacherRow.CompanyTeacherModelItem
import com.example.kurslinemobileapp.view.courseFmAc.CourseBusinessProfile
import com.squareup.picasso.Picasso
import java.util.Locale

class CompanyTeacherAdapter (private var items: ArrayList<CompanyTeacherModelItem>,private val voiceCallCourse:VoiceCallToCourses) :
    RecyclerView.Adapter<CompanyTeacherAdapter.CompanyTeacherHolder>() {
    lateinit var fullList :ArrayList<CompanyTeacherModelItem>
    var newList = arrayListOf<CompanyTeacherModelItem>()

    fun updateList(newList: List<CompanyTeacherModelItem>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    interface VoiceCallToCourses{
        fun clickOnCall(number:String,position: Int)
    }
    inner class CompanyTeacherHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val companyName: TextView = itemView.findViewById(R.id.companyTeacherCname)
        val companyCategory: TextView = itemView.findViewById(R.id.companyTeacherCategory)
        val companyImage: ImageView = itemView.findViewById(R.id.companyTeacherTabImage)
        val companyPhone: ImageView = itemView.findViewById(R.id.phoneCompanyTeacher)

        init {
            itemView.setOnClickListener(this)
            fullList = items
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
        holder.companyPhone.setOnClickListener {
            val phoneNumber = companyItem.companyPhone
            if (phoneNumber!=null){
                voiceCallCourse.clickOnCall(phoneNumber,position)
            }
            else{
                Log.d("LOG","Error")
            }
        }
        holder.bind(companyItem)

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
                        if (row.companyName.lowercase(Locale.ROOT)
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
                items = newList
                notifyDataSetChanged()
            }

        }
    }
}