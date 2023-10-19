package com.example.opentrivia.chat

import com.example.opentrivia.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ChatListaAmiciAdapter(private val friendsList: List<String>) : RecyclerView.Adapter<ChatListaAmiciAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.lista_amici_friend_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friendName = friendsList[position]
        holder.textViewFriendName.text = friendName
    }

    override fun getItemCount(): Int {
        return friendsList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewFriendName: TextView = itemView.findViewById(R.id.textViewFriendName)
    }
}

