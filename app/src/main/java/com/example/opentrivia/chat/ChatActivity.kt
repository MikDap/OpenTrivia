package com.example.opentrivia.chat

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.opentrivia.R
import com.google.firebase.database.FirebaseDatabase

class ChatActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_activity)


    }



    fun chiamaChat(idAmico: String, nomeAmico: String) {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = ChatFragment()
        fragmentTransaction.replace(R.id.fragmentContainerView2, fragment).addToBackStack("ChatListaAmici").commit()
    }
}
