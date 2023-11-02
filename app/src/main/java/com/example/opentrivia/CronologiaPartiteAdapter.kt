package com.example.opentrivia

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.opentrivia.listaAmici.FriendsAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CronologiaPartiteAdapter(private val partiteList: MutableMap<Int, PartitaTerminata>) : RecyclerView.Adapter<CronologiaPartiteAdapter.ViewHolder>() {
    private lateinit var database: FirebaseDatabase

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.visualizza_cronologia_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var partita = partiteList[position]
        if (partita != null) {
            holder.nomeAvv.text = partita.nomeAvv
            holder.scoreTextView1.text = partita.punteggioMio
            holder.scoreTextView3.text = partita.punteggioAvv
            holder.modalita.text = partita.modalita
        }

    }




    override fun getItemCount(): Int {
        return partiteList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
          val nomeAvv: TextView = itemView.findViewById(R.id.NomeAvversario)
          val scoreTextView1: TextView = itemView.findViewById(R.id.scoreTextView1)
        val scoreTextView3: TextView = itemView.findViewById(R.id.scoreTextView3)
        val modalita: TextView = itemView.findViewById(R.id.modalita)
    }

}
