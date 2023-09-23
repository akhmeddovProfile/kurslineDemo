package com.example.kurslinemobileapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.kurslinemobileapp.R
import com.example.kurslinemobileapp.api.announcement.getDetailAnnouncement.Comment

class CommentAdapter(private val comments: List<Comment>) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.bind(comment)
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val fullNameTextView: TextView = itemView.findViewById(R.id.fullNameTextView)
        private val contentTextView: TextView = itemView.findViewById(R.id.commentTextView)

        fun bind(comment: Comment) {
            fullNameTextView.text = comment.userFullName
            contentTextView.text = comment.commentContent
        }
    }

    fun commentAnnouncement(position: Int,isCommentedItemId:Int){
        comments[position].commentId=isCommentedItemId
        notifyItemChanged(position)
    }
}
