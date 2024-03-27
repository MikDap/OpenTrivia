package com.example.opentrivia.cronologia_partite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.opentrivia.R


class CronologiaPartiteFragment : Fragment() {


    private lateinit var recyclerView: RecyclerView

    // posizione, triple(nomeAvv, scoreio,scoreavv)
    val partiteList = mutableMapOf<Int, PartitaTerminata>()
    val adapter = CronologiaPartiteAdapter(partiteList, this)
 private val uid: String = FirebaseAuth.getInstance().currentUser?.uid.toString()
 private var partiteTerminateRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("partite terminate")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       val view= inflater.inflate(R.layout.visualizza_cronologia, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewFriends)



        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        recyclerView.adapter = adapter

        partiteTerminateRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(partite: DataSnapshot) {
                partiteList.clear() // Svuota la lista prima di popolarla con i nuovi dati
                var position = 0
                for (modalita in partite.children) {
                    for (difficolta in modalita.children) {
                        for (partita in difficolta.children) {

                            var ritirato = false
                            var avvRitirato = false
                            var avvEsiste = false
                            var partitaRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("partite terminate").child(
                                modalita.key.toString()).child(difficolta.key.toString()).child(partita.key.toString())

                            partitaRef.child("vista").setValue("si")

                            for (giocatore in partita.child("giocatori").children) {

                                var giocatore1 = giocatore.key.toString()
                                if(giocatore1 == uid){
                                    if (giocatore.hasChild("ritirato")){
                                        ritirato = true
                                    }
                                }
                                else {
                                    if (giocatore.hasChild("ritirato")){
                                        avvRitirato = true
                                    }
                                    avvEsiste = true

                                    var nomeAvv = giocatore.child("name").value.toString()
                                    var scoreMio = partita.child("esito").child("io").value.toString()
                                    var scoreAvv = partita.child("esito").child("avversario").value.toString()

                                    var partitaTer = PartitaTerminata(nomeAvv,scoreMio, scoreAvv, modalita.key.toString(),ritirato, avvRitirato)
                                    partiteList[position] = partitaTer

                                }

                            }
                            if (!avvEsiste){
                                var nomeAvv = "/"
                                var scoreMio = partita.child("esito").child("io").value.toString()
                                var scoreAvv = partita.child("esito").child("avversario").value.toString()

                                var partitaTer = PartitaTerminata(nomeAvv,scoreMio, scoreAvv, modalita.key.toString(), ritirato, false)
                                partiteList[position] = partitaTer
                            }
                            position++
                        }
                     }
                    }
                adapter.notifyDataSetChanged() // Aggiorna la RecyclerView quando i dati cambiano
                }

            override fun onCancelled(error: DatabaseError) {
                // gestione errore
            }
        })
    }




    fun setDrawable(holder: CronologiaPartiteAdapter.ViewHolder, esito: String){

        if (esito == "sconfitta") {
            val drawableId = R.drawable.custom_border_sconfitta
            val drawable = ResourcesCompat.getDrawable(resources, drawableId, null)
            holder.itemView.background = drawable
        }

        if (esito == "pareggio") {
            val drawableId = R.drawable.custom_border_pareggio
            val drawable = ResourcesCompat.getDrawable(resources, drawableId, null)
            holder.itemView.background = drawable
        }

        if (esito == "vittoria") {
            val drawableId = R.drawable.custom_border_vittoria
            val drawable = ResourcesCompat.getDrawable(resources, drawableId, null)
            holder.itemView.background = drawable
        }

    }
}