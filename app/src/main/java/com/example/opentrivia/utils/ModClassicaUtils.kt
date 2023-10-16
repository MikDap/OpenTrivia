package com.example.opentrivia.utils

import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ModClassicaUtils {



    companion object {


        fun updateScrollView(
            nomeAvversario: String, punteggioMio: Int, punteggioAvversario: Int, partita: String, database: FirebaseDatabase
        ) {
            val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
            val partiteInCorsoRef = database.getReference("users").child(uid).child("partite in corso")

            partiteInCorsoRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(giocatore: DataSnapshot) {
                    partiteInCorsoRef.child(partita).child("Avversario").setValue(nomeAvversario)
                    partiteInCorsoRef.child(partita).child("PunteggioMio").setValue(punteggioMio)
                    partiteInCorsoRef.child(partita).child("PunteggioAvversario").setValue(punteggioAvversario)


                }


                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }














        fun QuestaèLaRispostaCorretta (risposta: Button, rispostaCorretta: String): Boolean {


            if (risposta.text == rispostaCorretta) {
                risposta.setBackgroundColor(Color.LTGRAY)
                Handler(Looper.getMainLooper()).postDelayed({
                    risposta.setBackgroundColor(Color.GREEN)
                }, 500)
                return true
            }
                else {
                risposta.setBackgroundColor(Color.LTGRAY)
                Handler(Looper.getMainLooper()).postDelayed({
                    risposta.setBackgroundColor(Color.RED)
                }, 500)
                return false
            }
        }





















    }





}