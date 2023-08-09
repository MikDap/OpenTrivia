package com.example.opentrivia.listaAmici

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.opentrivia.R

class UsersAdapter(private val userList: List<String>, private val onUserClickListener: OnUserClickListener) : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.lista_amici_friend_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friendName = userList[position]
        holder.textViewFriendName.text = friendName
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         val textViewFriendName: TextView = itemView.findViewById(R.id.textViewFriendName)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val username = userList[position]
                    onUserClickListener.onUserClick(username)
                }
            }
        }
    }

    interface OnUserClickListener {
        fun onUserClick(username: String)
    }
}
