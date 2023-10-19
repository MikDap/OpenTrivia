package com.example.opentrivia.listaAmici

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

class ChatAdapter(private val messageList: List<String>) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private val VIEW_TYPE_MY_MESSAGE = 1
    private val VIEW_TYPE_OTHER_MESSAGE = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        // gameItemViewType
        val layoutRes = when (viewType) {
            VIEW_TYPE_MY_MESSAGE -> R.layout.chat_me
            VIEW_TYPE_OTHER_MESSAGE -> R.layout.chat_other
            else -> throw IllegalArgumentException("Invalid view type")
        }
        val itemView = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messageList[position]
        holder.textViewMessage.text = message
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

//    override fun getItemViewType(utente: String): Int {
        // Restituisci il tipo di vista in base all utente
        // Ad esempio, se il messaggio è dell'utente corrente, restituisci VIEW_TYPE_MY_MESSAGE,
        // altrimenti restituisci VIEW_TYPE_OTHER_MESSAGE
   //     return if (/* Condizione per determinare se il messaggio è dell'utente corrente */) {
  //          VIEW_TYPE_MY_MESSAGE
  //      } else {
   //         VIEW_TYPE_OTHER_MESSAGE
   //     }
  //  }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewMessage: TextView = itemView.findViewById(R.id.text_message)
    }
}

