package com.example.kurslinemobileapp.adapter

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.model.ContactItem

class ContactUsAdapter (private val contactList: List<ContactItem>) :
    RecyclerView.Adapter<ContactUsAdapter.ContactViewHolder>() {

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactImage: ImageView = itemView.findViewById(R.id.imageViewContact)
        val contactName: TextView = itemView.findViewById(R.id.textViewContact)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.contact_row, parent, false)
        return ContactViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val currentItem = contactList[position]
        holder.contactImage.setImageResource(currentItem.imageUrl)
        holder.contactName.text = currentItem.name

        holder.itemView.setOnClickListener {
            if (position==0){
               // val intent=Intent(holder.itemView.context, WriteUs::class.java)
                //holder.itemView.context.startActivity(intent)
            }
            else if(position==1){
                openInstagram(holder.itemView.context)
            }
            else if (position==2){
                openFaceook(holder.itemView.context)
            }
        }
    }

    override fun getItemCount() = contactList.size

    private fun openInstagram(context: Context) {
        val instagramAppUrl = "https://www.instagram.com/aimtech_az/"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(instagramAppUrl))
        intent.setPackage("com.instagram.android")

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // If the Instagram app is not installed, open the Instagram website
            val instagramWebUrl = "https://www.instagram.com/aimtech_az/"
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(instagramWebUrl))
            context.startActivity(webIntent)
        }

    }
    private fun openFaceook(context: Context) {
        val instagramAppUrl = "https://www.facebook.com/your_instagram_username/"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(instagramAppUrl))
        intent.setPackage("com.facebook.android")

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // If the Instagram app is not installed, open the Instagram website
            val instagramWebUrl = "https://www.instagram.com/your_instagram_username/"
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(instagramWebUrl))
            context.startActivity(webIntent)
        }
    }
}