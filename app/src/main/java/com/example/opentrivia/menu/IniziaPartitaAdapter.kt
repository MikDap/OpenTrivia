package com.example.opentrivia.menu

import com.example.opentrivia.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class IniziaPartitaAdapter(private val friendsList: Map<String, String>, private val iniziaPartitaFragment: IniziaPartita) : RecyclerView.Adapter<IniziaPartitaAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.menu_inizia_partita_amico_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friendId = friendsList.keys.elementAt(position)
        val friendName = friendsList[friendId]
        holder.textViewFriendName.text = friendName
    }

    override fun getItemCount(): Int {
        return friendsList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewFriendName: TextView = itemView.findViewById(R.id.textViewFriendName)
        val check: ImageView = itemView.findViewById(R.id.check)
        val border: ImageView = itemView.findViewById(R.id.border)


        init {
            check.visibility = View.INVISIBLE

            // Aggiungi il click listener a border
            border.setOnClickListener {

                iniziaPartitaFragment.nomeAvversario(textViewFriendName.text.toString())

                // Imposta check visibile solo per questa posizione e invisibile per le altre
                for (i in friendsList.keys.indices) {
                    if (i == adapterPosition) {
                        val key = friendsList.keys.elementAt(i)
                        iniziaPartitaFragment.avversarioID = key
                        iniziaPartitaFragment.avversarioNome = friendsList[key].toString()
                        // Posizione corrente, setta check visibile
                        check.visibility = View.VISIBLE
                    } else {
                        // Altrimenti, setta check invisibile
                        (itemView.parent as RecyclerView).findViewHolderForAdapterPosition(i)
                            ?.let { holder ->
                                (holder as ViewHolder).check.visibility = View.INVISIBLE
                            }
                    }
                }
            }

        }
    }
}

