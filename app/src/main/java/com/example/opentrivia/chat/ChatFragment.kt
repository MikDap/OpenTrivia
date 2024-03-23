package com.example.opentrivia.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.opentrivia.R
import com.example.opentrivia.listaSeguiti.ChatAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class ChatFragment : Fragment() {


    private lateinit var database: FirebaseDatabase
    private lateinit var nome : TextView
    private lateinit var editText: EditText
    lateinit var recyclerView: RecyclerView
    private lateinit var nomeAvversario: TextView
    private lateinit var send: Button
    private var userIdOther: String = ""
    private var usernameOther: String = ""
    private var chatID: String = ""
    private var uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private var displayname = FirebaseAuth.getInstance().currentUser?.displayName.toString()
    private var chatRef = FirebaseDatabase.getInstance().getReference("chat")
    lateinit var adapter: ChatAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_chat, container, false)

        nome = view.findViewById(R.id.nome)
        nomeAvversario = view.findViewById(R.id.nomeutente)
        editText = view.findViewById(R.id.edit_message)
        recyclerView = view.findViewById(R.id.recycler_chat)
        send = view.findViewById(R.id.button_send)

        arguments?.let {
            userIdOther = it.getString("userId").toString()
            usernameOther = it.getString("username").toString()
            chatID = it.getString("chatID").toString()
        }

        database = FirebaseDatabase.getInstance()

        nomeAvversario.text = usernameOther

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        adapter = ChatAdapter(chatID,userIdOther, usernameOther, this)
        adapter.chatID = chatID
        recyclerView.adapter = adapter


        send.setOnClickListener{

            val timestamp = System.currentTimeMillis()
            val messaggio = editText.text.toString()

            editText.text.clear()

            var i = adapter.itemCount

            val messageID = (i + 1).toString()

            // Chiamata alla funzione scriviMessaggioDatabase con l'ID univoco
            scriviMessaggioDatabase(chatID, messageID, messaggio, timestamp) {
                ->  adapter.notifyDataSetChanged()
                recyclerView.smoothScrollToPosition(adapter.itemCount - 1)
            }
        }
    }






    fun scriviMessaggioDatabase(chatID: String, messageID: String, messaggio: String, timestamp: Long, callback:() -> Unit) {
        // Utilizza messageID come chiave per il messaggio nel database
        chatRef.child(chatID).child("messaggi").child(messageID).child("testo").setValue(messaggio)
        chatRef.child(chatID).child("messaggi").child(messageID).child("mittente").setValue(uid)
        val timestamp = chatRef.child(chatID).child("messaggi").child(messageID).child("timestamp").setValue(timestamp)

        timestamp.addOnCompleteListener{
            callback()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()

        adapter.removeListener()
    }

}