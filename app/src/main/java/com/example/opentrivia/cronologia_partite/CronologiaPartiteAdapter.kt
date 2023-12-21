package com.example.opentrivia.cronologia_partite

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.opentrivia.R
import com.google.firebase.database.FirebaseDatabase

class CronologiaPartiteAdapter(private val partiteList: MutableMap<Int, PartitaTerminata>, private val cronologiaPartite: CronologiaPartite) : RecyclerView.Adapter<CronologiaPartiteAdapter.ViewHolder>() {
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

            scriviModalita(holder, partita)

            coloraSfondoPartita(holder,partita)

        }

    }




    override fun getItemCount(): Int {
        return partiteList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
          val nomeAvv: TextView = itemView.findViewById(R.id.NomeAvversario)
          val scoreTextView1: TextView = itemView.findViewById(R.id.scoreTextView1)
        val scoreTextView2: TextView = itemView.findViewById(R.id.scoreTextView2)
        val scoreTextView3: TextView = itemView.findViewById(R.id.scoreTextView3)
        var modalita: TextView = itemView.findViewById(R.id.modalita)
        val ritiratoView: TextView = itemView.findViewById(R.id.ritiratoView)
    }


    fun coloraSfondoPartita(holder: ViewHolder, partita: PartitaTerminata){



        var punteggioMio = partita.punteggioMio.toInt()
        var punteggioAvv = partita.punteggioAvv.toInt()
        var ritirato = partita.ritirato
        var avvRitirato = partita.avvRitirato

        if (ritirato){
            holder.ritiratoView.visibility = View.VISIBLE
            holder.scoreTextView1.visibility = View.INVISIBLE
            holder.scoreTextView2.visibility = View.INVISIBLE
            holder.scoreTextView3.visibility = View.INVISIBLE

            cronologiaPartite.setDrawable(holder, "sconfitta")

        }
        else if (avvRitirato){
            holder.ritiratoView.visibility = View.VISIBLE
            holder.scoreTextView1.visibility = View.INVISIBLE
            holder.scoreTextView2.visibility = View.INVISIBLE
            holder.scoreTextView3.visibility = View.INVISIBLE
            cronologiaPartite.setDrawable(holder, "vittoria")
        }

        else {

            if (punteggioMio > punteggioAvv) {

                cronologiaPartite.setDrawable(holder, "vittoria")
            }
            if (punteggioMio < punteggioAvv) {

                cronologiaPartite.setDrawable(holder, "sconfitta")
            }
            if (punteggioMio == punteggioAvv) {

                cronologiaPartite.setDrawable(holder, "pareggio")
            }
        }
    }

    fun scriviModalita(holder: ViewHolder, partita: PartitaTerminata){

        if (partita.modalita == "classica"){
            holder.modalita.text = "Classica"
        }
        if (partita.modalita == "argomento singolo"){
            holder.modalita.text = "Argomento Singolo"
        }
        if (partita.modalita == "a tempo"){
            holder.modalita.text = "A Tempo"
        }

    }
}
