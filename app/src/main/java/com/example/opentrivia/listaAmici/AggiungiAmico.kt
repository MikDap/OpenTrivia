package com.example.opentrivia.listaAmici

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.opentrivia.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class AggiungiAmico : Fragment(), UsersAdapter.OnUserClickListener {

    private lateinit var database: FirebaseDatabase
    private lateinit var editText: EditText
    private lateinit var buttonSearchFriend: Button
    private lateinit var recyclerView: RecyclerView

    private val userList = mutableListOf<String>()
    val layoutManager = LinearLayoutManager(requireContext())
    val adapter = UsersAdapter(userList, this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
      val view = inflater.inflate(R.layout.lista_amici_aggiungi_amico, container, false)

        editText = view.findViewById(R.id.editTextFriendName)
        buttonSearchFriend = view.findViewById(R.id.buttonSearchFriend)
        recyclerView = view.findViewById(R.id.recyclerViewFriends)

        database = FirebaseDatabase.getInstance()


        recyclerView.layoutManager = layoutManager

        recyclerView.adapter = adapter

        buttonSearchFriend.setOnClickListener {
            val friendNameToSearch = editText.text.toString().trim()
            searchFriend(friendNameToSearch)
        }



        return view
    }


    private fun searchFriend(friendName: String) {
        val usersRef = database.getReference("users")
        userList.clear()
        usersRef.orderByValue().startAt(friendName).endAt(friendName + "\uf8ff")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (friendSnapshot in snapshot.children) {
                        val friendName = friendSnapshot.getValue(String::class.java)
                        friendName?.let {
                            userList.add(it)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Gestisci l'errore (opzionale)
                }
            })
    }


    override fun onUserClick(username: String) {
        // Qui puoi gestire l'evento di clic su un utente.
        // Ad esempio, puoi aggiungere l'utente alla lista degli amici o eseguire altre azioni.
    }

}