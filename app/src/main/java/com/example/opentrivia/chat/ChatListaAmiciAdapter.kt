package com.example.opentrivia.chat

import com.example.opentrivia.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatListaAmiciAdapter(private val userKeyMap: Map<String, String>, private val clickListener: ChatListaAmiciFragment.OnAmicoClickListener) : RecyclerView.Adapter<ChatListaAmiciAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.chat_amico_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userId = userKeyMap.keys.elementAt(position)
        val username = userKeyMap[userId]
        holder.amico.text = username
        holder.chat.setOnClickListener {
            if (username != null) {
                clickListener.onAmicoClick(userId, username)
            }
        }
    }

    override fun getItemCount(): Int {
        return userKeyMap.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val amico: TextView = itemView.findViewById(R.id.textViewFriendName)
        val chat: ImageView = itemView.findViewById(R.id.ChatImage)


    }




}

