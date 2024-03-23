package com.example.opentrivia.listaSeguiti

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

class SeguiUtenteAdapter(private val userKeyMap: Map<String, String>) : RecyclerView.Adapter<SeguiUtenteAdapter.ViewHolder>() {

    private lateinit var database: FirebaseDatabase

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.lista_seguiti_utente_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userId = userKeyMap.keys.elementAt(position)
        val username = userKeyMap[userId]
        holder.textViewFriendName.text = username

        checkIfUserIsFriend(userId) { isFriend ->
            // Qui puoi utilizzare il valore di isFriend

            if (isFriend) {
                holder.seguiButton.visibility = View.INVISIBLE
                holder.seguitoImage.visibility = View.VISIBLE
            } else {
                holder.seguiButton.visibility = View.VISIBLE
                holder.seguitoImage.visibility = View.INVISIBLE
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
        val seguiButton: ImageView = itemView.findViewById(R.id.clickableImage)
        val seguitoImage: ImageView = itemView.findViewById(R.id.CheckImage)

        init {

            seguiButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {

                    seguiButton.visibility = View.INVISIBLE
                    seguitoImage.visibility = View.VISIBLE

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
