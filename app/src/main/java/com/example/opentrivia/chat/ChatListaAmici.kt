package com.example.opentrivia.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.MenuProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.opentrivia.R
import com.example.opentrivia.listaAmici.ChatAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ChatListaAmici : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val friendsList = mutableListOf<String>()
    val adapter = ChatListaAmiciAdapter(friendsList)
    private val uid: String = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private var amiciRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("amici")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat_lista_amici, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewFriends)

        //INSERIRE CHE QUANDO SCHIACCIO AMICO MI APRE CHATFRAGMENT
        return view
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        recyclerView.adapter = adapter


        // Aggiungi un ValueEventListener per ottenere i dati degli amici da Firebase
        amiciRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                friendsList.clear() // Svuota la lista prima di popolarla con i nuovi dati
                for (amicoSnapshot in snapshot.children) {
                    val amico = amicoSnapshot.getValue(String::class.java)
                    if (amico != null) {
                        friendsList.add(amico)
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