package com.example.opentrivia.listaSeguiti

import com.example.opentrivia.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ListaSeguitiAdapter(private val friendsList: List<String>) : RecyclerView.Adapter<ListaSeguitiAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.lista_seguiti_utente_item, parent, false)
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
        val addFriendButton: ImageView = itemView.findViewById(R.id.clickableImage)
        val isFriendCheck: ImageView = itemView.findViewById(R.id.CheckImage)


        init {
            addFriendButton.visibility = View.INVISIBLE
            isFriendCheck.visibility = View.INVISIBLE

        }



    }
}

