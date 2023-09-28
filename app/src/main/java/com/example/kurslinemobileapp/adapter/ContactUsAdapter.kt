package com.example.kurslinemobileapp.adapter

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.app.kurslinemobileapp.R
import com.example.kurslinemobileapp.model.ContactItem
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.view.MainActivity
import com.example.kurslinemobileapp.view.fragments.ContactUsFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactUsAdapter (private val contactList: List<ContactItem>) :
    RecyclerView.Adapter<ContactUsAdapter.ContactViewHolder>() {



    val compositeDisposable=CompositeDisposable()
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
              openwWriteUs(holder.itemView.context)
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

    @SuppressLint("MissingInflatedId")
    private fun openwWriteUs(context: Context){
            if (context is MainActivity) {
                val fragmentManager = context.supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()

                val writeUsFragment = ContactUsFragment()

                fragmentTransaction.replace(R.id.settingsLayout, writeUsFragment)
                fragmentTransaction.addToBackStack(null) // Optional: Add to back stack
                fragmentTransaction.commit()
            }
    }


    private fun openInstagram(context: Context) {
        val instagramAppUrl = "https://www.instagram.com/kursline.az/"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(instagramAppUrl))
        intent.setPackage("com.instagram.android")

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // If the Instagram app is not installed, open the Instagram website
            val instagramWebUrl = "https://www.instagram.com/kursline.az/"
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(instagramWebUrl))
            context.startActivity(webIntent)
        }

    }
    private fun openFaceook(context: Context) {
        val instagramAppUrl = "https://www.facebook.com/profile.php?id=100063797953572"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(instagramAppUrl))
        intent.setPackage("com.facebook.android")

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // If the Instagram app is not installed, open the Instagram website
            val instagramWebUrl = "https://www.facebook.com/profile.php?id=100063797953572"
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(instagramWebUrl))
            context.startActivity(webIntent)
        }
    }
}