package com.example.opentrivia.listaSeguiti

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.opentrivia.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class SeguiUtenteFragment : Fragment() {

    private lateinit var database: FirebaseDatabase
    private lateinit var editText: EditText
    private lateinit var buttonSearchFriend: Button
    private lateinit var recyclerView: RecyclerView

    private val userKeyMap = mutableMapOf<String, String>()  // Chiave: ID utente, Valore: Nome amico

    val adapter = SeguiUtenteAdapter(userKeyMap)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.lista_seguiti_segui_utente, container, false)

        editText = view.findViewById(R.id.editTextFriendName)
        buttonSearchFriend = view.findViewById(R.id.buttonSearchFriend)
        recyclerView = view.findViewById(R.id.recyclerViewUsers)

        database = FirebaseDatabase.getInstance()


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager


        recyclerView.adapter = adapter

        buttonSearchFriend.setOnClickListener {
            val friendNameToSearch = editText.text.toString().trim()
            Log.d("amico0", friendNameToSearch)
            cercaUtenti(friendNameToSearch)
        }
    }
    private fun cercaUtenti(friendName: String) {
      val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val usersRef = database.getReference("users")
        userKeyMap.clear()
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (userSnapshot in snapshot.children) {
                        val userId = userSnapshot.key  // ID utente
                        val username = userSnapshot.child("username").getValue(String::class.java)

                        if (userId != uid && userId != null && username != null && username.contains(friendName, ignoreCase = true)) {
                            userKeyMap[userId] = username
                        }
                        Log.d("amico", friendName)
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Gestisci l'errore (opzionale)
                }
            })
    }


}