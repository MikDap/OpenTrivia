package com.example.opentrivia

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.opentrivia.listaAmici.FriendsAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class CronologiaPartite : Fragment() {


    private lateinit var recyclerView: RecyclerView

    // posizione, triple(nomeAvv, scoreio,scoreavv)
    val partiteList = mutableMapOf<Int, Triple<String, String, String>>()
    val adapter = CronologiaPartiteAdapter(partiteList)
 private val uid: String = FirebaseAuth.getInstance().currentUser?.uid.toString()
 private var partiteTerminateRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("partite terminate")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

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

        // Aggiungi un ValueEventListener per ottenere le partite terminate da Firebase
        partiteTerminateRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(partite: DataSnapshot) {
                partiteList.clear() // Svuota la lista prima di popolarla con i nuovi dati
                var position = 0
                for (modalita in partite.children) {
                    for (difficolta in modalita.children) {
                        for (partita in difficolta.children) {
                            for (giocatore in partita.child("giocatori").children) {

                                var giocatore1 = giocatore.key.toString()

                                if (giocatore1 != uid){
                                    var nomeAvv = giocatore.child("name").value.toString()
                                    var scoreMio = partita.child("esito").child("io").value.toString()
                                    var scoreAvv = partita.child("esito").child("io").value.toString()
                                    partiteList[position] = Triple( nomeAvv, scoreMio, scoreAvv)
                                }

                            }
                            position++
                        }
                     }
                    }
                adapter.notifyDataSetChanged() // Aggiorna la RecyclerView quando i dati cambiano
                }

            override fun onCancelled(error: DatabaseError) {
                // Gestisci l'errore, se necessario
            }
        })
    }

}