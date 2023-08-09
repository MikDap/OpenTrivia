package com.example.opentrivia.chat

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.opentrivia.R
import com.google.firebase.database.FirebaseDatabase

class ChatActivity : AppCompatActivity() {

     private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_activity)


        database = FirebaseDatabase.getInstance()
        val chatRef = database.getReference("partite").child("argomento singolo")

        val editText = findViewById<EditText>(R.id.edit_gchat_message)
        val sendButton = findViewById<Button>(R.id.button_gchat_send)

        sendButton.setOnClickListener {
            val testo = editText.text


        }





    }
}
