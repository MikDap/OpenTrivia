package com.example.opentrivia.listaAmici

import android.util.Log
import com.example.opentrivia.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.opentrivia.chat.ChatFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.Calendar

class ChatAdapter(chatID: String, userId: String, username: String, chatFragment: ChatFragment) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private val VIEW_TYPE_MY_MESSAGE = 1
    private val VIEW_TYPE_OTHER_MESSAGE = 2
    private var uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private var chatRef = FirebaseDatabase.getInstance().getReference("chat")
    private val userMessages = mutableMapOf<Int, Triple<String, String, Long>>()
    private lateinit var chatRefListener: ValueEventListener
    var controlloData = "0"

    lateinit var chatID: String
    init {
        this.chatID = chatID
        leggiMessaggiDatabase(){ ->
            if (chatFragment.adapter.itemCount > 1) {
                chatFragment.recyclerView.smoothScrollToPosition(chatFragment.adapter.itemCount - 1)
            }
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        // gameItemViewType
        val layoutRes = when (viewType) {
            VIEW_TYPE_MY_MESSAGE -> R.layout.chat_me
            VIEW_TYPE_OTHER_MESSAGE -> R.layout.chat_other
            else -> throw IllegalArgumentException("Invalid view type")
        }

        val itemView = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
      return userMessages.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
     //   val message = messageList[position]
      //  holder.textViewMessage.text = message
        val messaggio = userMessages.keys.elementAt(position)
        val testo = userMessages[position]?.first
        val mittente = userMessages[position]?.second
        val timestamp = userMessages[position]?.third

        // Crea un oggetto Calendar e impostalo con il timestamp
        val calendar = Calendar.getInstance()
        if (timestamp != null) {
            calendar.timeInMillis = timestamp
        }

        // Ottieni il giorno, il mese e l'orario
        val giorno = calendar.get(Calendar.DAY_OF_MONTH)
        val mese = calendar.get(Calendar.MONTH) + 1  // I mesi in Calendar vanno da 0 a 11
        val orario = "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}:${calendar.get(Calendar.SECOND)}"

        val nomeMese = getNomeMese(mese)

        val data = nomeMese + " " + giorno

        if (data != controlloData) {
            holder.data.text = data
        } else {
            holder.data.text = ""
        }
        holder.orario.text= orario
        holder.messaggio.text = testo

        controlloData = data
    }



   override fun getItemViewType(position: Int): Int {
        // Restituisci il tipo di vista in base all utente
        // se il messaggio è dell'utente corrente, restituisci VIEW_TYPE_MY_MESSAGE,
        // altrimenti restituisci VIEW_TYPE_OTHER_MESSAGE

       val messaggio = userMessages[position]
       val mittenteID = messaggio?.second // Ottieni l'ID del mittente dal Triple

       return if (mittenteID == uid) {
           VIEW_TYPE_MY_MESSAGE // Se il mittente è l'utente corrente
       } else {
           VIEW_TYPE_OTHER_MESSAGE // Altrimenti, se il mittente non è l'utente corrente
       }

   }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val data: TextView = itemView.findViewById(R.id.text_data)
        val messaggio: TextView = itemView.findViewById(R.id.text_message)
        val orario: TextView = itemView.findViewById(R.id.text_timestamp)


    }




    fun leggiMessaggiDatabase(callback: () -> Unit){

        chatRefListener = chatRef.child(chatID).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(listachat: DataSnapshot) {
                val messaggichat = listachat.child("messaggi")
                var position = 0

                for (messaggio in messaggichat.children) {

                    val testo = messaggio.child("testo").value.toString()
                    val mittente = messaggio.child("mittente").value.toString()
                    val timestamp= messaggio.child("timestamp").value
                    Log.d("timestamp", timestamp.toString())

                    Log.d("testo",testo)
                    Log.d("mittente",mittente)
                    userMessages[position] = Triple(testo, mittente, timestamp) as Triple<String, String, Long>
                    position++

                }


                // Notifica l'adattatore che i dati sono stati modificati
                notifyDataSetChanged()

                callback()

            }

            override fun onCancelled(error: DatabaseError) {
                // Gestisci l'errore, se necessario
            }
        })


    }



    fun getNomeMese(numeroMese: Int): String {
        return when (numeroMese) {
            1 -> "Gennaio"
            2 -> "Febbraio"
            3 -> "Marzo"
            4 -> "Aprile"
            5 -> "Maggio"
            6 -> "Giugno"
            7 -> "Luglio"
            8 -> "Agosto"
            9 -> "Settembre"
            10 -> "Ottobre"
            11 -> "Novembre"
            12 -> "Dicembre"
            else -> "Mese non valido"
        }
    }


    fun removeListener(){
        chatRef.child(chatID).removeEventListener(chatRefListener)
    }
}

