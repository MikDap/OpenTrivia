package com.example.opentrivia.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

import com.example.opentrivia.R
import com.example.opentrivia.listaAmici.ChatAdapter
import com.example.opentrivia.listaAmici.UsersAdapter
import com.google.firebase.database.FirebaseDatabase


class ChatFragment : Fragment() {


    private lateinit var database: FirebaseDatabase
    private lateinit var nome : TextView
    private lateinit var editText: EditText
    private lateinit var recyclerView: RecyclerView

    //l'utente lo passiamo a sta classe quando la istanziamo in chatListaAmici
private lateinit var utente:String
   // val adapter = ChatAdapter(utente)

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
        editText = view.findViewById(R.id.edit_message)
        recyclerView = view.findViewById(R.id.recycler_chat)



        database = FirebaseDatabase.getInstance()

        return view
    }


}