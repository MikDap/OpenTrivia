package com.example.opentrivia.utils

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ModClassicaUtils {



    companion object {


        fun updateScrollView(
            nomeAvversario: String,idAvversario: String, punteggioMio: Int, punteggioAvversario: Int, partita: String, difficolta: String, database: FirebaseDatabase
        ) {
            val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
            val nomeMio = FirebaseAuth.getInstance().currentUser?.displayName.toString()
            val partiteInCorsoMioRef = database.getReference("users").child(uid).child("partite in corso")
            val partiteInCorsoAvversarioRef = database.getReference("users").child(idAvversario).child("partite in corso")

                    partiteInCorsoMioRef.child(partita).child("Avversario").setValue(nomeAvversario)
                    partiteInCorsoMioRef.child(partita).child("PunteggioMio").setValue(punteggioMio)
                    partiteInCorsoMioRef.child(partita).child("PunteggioAvversario").setValue(punteggioAvversario)
                    partiteInCorsoMioRef.child(partita).child("difficolta").setValue(difficolta)



            if (nomeAvversario != "-") {
                        partiteInCorsoAvversarioRef.child(partita).child("Avversario")
                            .setValue(nomeMio)
                        partiteInCorsoAvversarioRef.child(partita).child("PunteggioMio")
                            .setValue(punteggioAvversario)
                        partiteInCorsoAvversarioRef.child(partita).child("PunteggioAvversario")
                            .setValue(punteggioMio)
                        partiteInCorsoAvversarioRef.child(partita).child("difficolta").setValue(difficolta)
                    }

            }







// usato quando il giocatore risponde correttamente ( dobbiamo usarla per illuminare i 3 quadratini sotto)
        fun updateContatoreRisposteCorrette(
            giocatoriRef: DatabaseReference, callback: () -> Unit
        ) {
            giocatoriRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(giocatori: DataSnapshot) {

                    val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()

                    if (giocatori.child(uid).hasChild("risposteTotCorrette")) {

                        var risposte_corrette =
                            giocatori.child(uid).child("risposteTotCorrette").value.toString().toInt()

                        risposte_corrette++

                        giocatoriRef.child(uid).child("risposteTotCorrette").setValue(risposte_corrette)
                    } else {

                        giocatoriRef.child(uid).child("risposteTotCorrette").setValue(1)
                    }
                        callback()
                }


                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }




//    RITORNA DUE ARRAYLIST, UNA CONTENENTE I MIEI ARGOMENTI CONQUISTATI, L'ALTRA DELL'AVVERSARIO

        fun getArgomentiConquistati(giocatoriRef: DatabaseReference, callback: (ArrayList<String>, ArrayList<String>) -> Unit)
        {

            var argomentiMiei = ArrayList<String>()
            var argomentiAvversario = ArrayList<String>()

            giocatoriRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(giocatori: DataSnapshot) {

                    val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()


                    for (giocatore in giocatori.children) {

                        val giocatore1 = giocatore.key.toString()


                        if (giocatore.hasChild("ArgomentiConquistati")) {
                            if (giocatore1 == uid) {
                                for (argomento in giocatore.child("ArgomentiConquistati").children) {
                                    val nomeArgomento = argomento.key.toString()
                                    Log.d("nomeArgomento",nomeArgomento)
                                    argomentiMiei.add(nomeArgomento)
                                }
                            }
                            else {
                                for (argomento in giocatore.child("ArgomentiConquistati").children) {
                                    val nomeArgomento = argomento.key.toString()
                                    argomentiAvversario.add(nomeArgomento)
                                }
                            }
                        }

                    }
                    callback.invoke(argomentiMiei, argomentiAvversario)
                }


                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }





// LEGGE LO STATO DELLE RISPOSTE

        fun leggiRisposteCorrette(
            giocatoreRef: DatabaseReference, callback: (statoRisposte: Int) -> Unit
        ) {
            giocatoreRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(giocatore: DataSnapshot) {

                   var statoRisposte = 0
                    if (giocatore.hasChild("risposteTotCorrette")) {

                         statoRisposte = giocatore.child("risposteTotCorrette").value.toString().toInt()

                    }
                    callback(statoRisposte)
                }


                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }





    }

}