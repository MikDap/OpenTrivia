package com.example.opentrivia.listaSeguiti

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.opentrivia.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ListaSeguitiFragment : Fragment() {
    private lateinit var addFriendButton: Button
    private lateinit var textViewNessunSeguito: TextView
    private lateinit var recyclerView: RecyclerView
    private val seguitiLista = mutableListOf<String>()
    val adapter = ListaSeguitiAdapter(seguitiLista)
    private val uid: String = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private val seguitiRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("amici")

    private var qualcunoSeguito = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.lista_seguiti_fragment, container, false)
        addFriendButton = view.findViewById(R.id.buttonAddFriend)
        recyclerView = view.findViewById(R.id.recyclerViewFriends)
        textViewNessunSeguito = view.findViewById(R.id.textViewNoFriends)

        addFriendButton.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_listaAmiciFragment2_to_aggiungiAmico2)}
        return view
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        recyclerView.adapter = adapter


            // Aggiungi un ValueEventListener per ottenere i dati degli amici da Firebase
            seguitiRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    seguitiLista.clear() // Svuota la lista prima di popolarla con i nuovi dati
                    for (amicoSnapshot in snapshot.children) {
                        val amico = amicoSnapshot.getValue(String::class.java)
                        if (amico != null) {
                            seguitiLista.add(amico)
                            qualcunoSeguito = true
                        }
                    }
                    adapter.notifyDataSetChanged() // Aggiorna la RecyclerView quando i dati cambiano

                    if (!qualcunoSeguito) {
                        textViewNessunSeguito.visibility = View.VISIBLE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Gestisci l'errore, se necessario
                }
            })
    }
}