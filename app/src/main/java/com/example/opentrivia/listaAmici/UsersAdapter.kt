package com.example.opentrivia.listaAmici

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.opentrivia.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UsersAdapter(private val userKeyMap: Map<String, String>) : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    private lateinit var database: FirebaseDatabase

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.lista_amici_friend_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userId = userKeyMap.keys.elementAt(position)
        val username = userKeyMap[userId]
        holder.textViewFriendName.text = username

        checkIfUserIsFriend(userId) { isFriend ->
            // Qui puoi utilizzare il valore di isFriend

            if (isFriend) {
                holder.addFriendButton.visibility = View.INVISIBLE
                holder.isFriendCheck.visibility = View.VISIBLE
            } else {
                holder.addFriendButton.visibility = View.VISIBLE
                holder.isFriendCheck.visibility = View.INVISIBLE
            }
        }
    }



    private fun checkIfUserIsFriend(friendUserId: String, callback: (Boolean) -> Unit) {
        // Esegui la tua logica per verificare se l'utente è amico
        // accedere al percorso appropriato nel database e fare la verifica

        database = FirebaseDatabase.getInstance()
        var isFriend = false

        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val amiciRef = database.getReference("users").child(uid).child("amici")
        amiciRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (userSnapshot in snapshot.children) {
                    val userId = userSnapshot.key  // ID utente
                    if (userId == friendUserId) {
                     isFriend = true
                        break
                    }
                }
                callback(isFriend)
            }

            override fun onCancelled(error: DatabaseError) {
                // Gestisci l'errore (opzionale)
                callback(false)
            }
        })
    }

    override fun getItemCount(): Int {
        return userKeyMap.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         val textViewFriendName: TextView = itemView.findViewById(R.id.textViewFriendName)
        val addFriendButton: ImageView = itemView.findViewById(R.id.clickableImage)
        val isFriendCheck: ImageView = itemView.findViewById(R.id.CheckImage)

        init {

            addFriendButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {

                    addFriendButton.visibility = View.INVISIBLE
                    isFriendCheck.visibility = View.VISIBLE

                    val userId = userKeyMap.keys.elementAt(position)
                    val username = userKeyMap[userId]
                    // Esegui un'azione quando l'utente fa clic sull'ImageView

                    database = FirebaseDatabase.getInstance()
                    val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()

                    var AmiciRef = database.getReference("users").child(uid).child("amici")
                    AmiciRef.child(userId).setValue(username)




                }
            }
        }
    }

}
