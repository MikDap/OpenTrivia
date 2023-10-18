package com.example.opentrivia.utils

import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
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












//verifa se questa è la risposta corretta, restituisce true o false

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
























// ottengo i dati della partita per poterli poi inserire successivamente nella scrollview

        //AGGIORNARE: ottiene numero argomenti conquistati non quali
        fun ottieniNomeAvversario_e_argomentiConquistati(
            giocatoriRef: DatabaseReference,
            callback: (nomeAvversario: String, argomenti_conquistati_miei: Int, argomenti_conquistati_avversario: Int) -> Unit
        ) {
            giocatoriRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(giocatori: DataSnapshot) {

                    val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
                    var nomeAvversario = "-"
                    var argomenti_conquistati_miei = 0
                    var  argomenti_conquistati_avversario = 0

                    for (giocatore in giocatori.children) {

                        var giocatore1 = giocatore.key.toString()

                        if (giocatore.hasChild("ArgomentiConquistati")) {
                            if (giocatore1 == uid) {
                                for (argomento in giocatore.child("ArgomentiConquistati").children) {
                                    argomenti_conquistati_miei++
                                }
                            }
                            else {
                                 nomeAvversario = giocatore.child("name").value.toString()
                                for (argomento in giocatore.child("ArgomentiConquistati").children) {
                                    argomenti_conquistati_avversario++
                                }
                            }
                        }

                    }

                    callback(nomeAvversario, argomenti_conquistati_miei, argomenti_conquistati_avversario)
                }


                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }





//    RITORNA DUE ARRAYLIST, UNA CONTENENTE I MIEI ARGOMENTI CONQUISTATI, L'ALTRA DELL'AVVERSARIO

        fun QualiArgomentiConquistati(giocatoriRef: DatabaseReference, callback: (ArrayList<String>, ArrayList<String>) -> Unit)
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











        fun updateRisposte(
            risposteRef: DatabaseReference, tipo: String
        ) {
            risposteRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {



                    if (tipo == "corretta") {
                        if (dataSnapshot.child("risposteCorrette").exists()) {
                            // Il dato esiste nel database
                            var punti = dataSnapshot.child("risposteCorrette").value.toString().toInt()
                            punti++
                            risposteRef.child("risposteCorrette").setValue(punti)

                        } else {
                            // Il dato non esiste nel database, quindi scrivi qualcosa
                            risposteRef.child("risposteCorrette").setValue(1)
                        }


                        if (dataSnapshot.child("risposteTotali").exists()) {
                            // Il dato esiste nel database
                            var punti = dataSnapshot.child("risposteTotali").value.toString().toInt()
                            punti++
                            risposteRef.child("risposteTotali").setValue(punti)



                        } else {
                            // Il dato non esiste nel database, quindi scrivi qualcosa
                            risposteRef.child("risposteTotali").setValue(1)
                        }
                    }





                    else if (tipo == "sbagliata") {

                        if (dataSnapshot.child("risposteSbagliate").exists()) {
                            // Il dato esiste nel database
                            var punti = dataSnapshot.child("risposteSbagliate").value.toString().toInt()
                            punti++
                            risposteRef.child("risposteSbagliate").setValue(punti)

                        } else {
                            // Il dato non esiste nel database, quindi scrivi qualcosa
                            risposteRef.child("risposteSbagliate").setValue(1)
                        }


                        if (dataSnapshot.child("risposteTotali").exists()) {
                            // Il dato esiste nel database
                            var punti = dataSnapshot.child("risposteTotali").value.toString().toInt()
                            punti++
                            risposteRef.child("risposteTotali").setValue(punti)

                        } else {
                            // Il dato non esiste nel database, quindi scrivi qualcosa
                            risposteRef.child("risposteTotali").setValue(1)
                        }
                    }


                    //qua il codice per salvare sul database i dati nel nodo users per utilizzarli nella scrollview


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











        fun ottieniNomeAvversario(
            giocatoriRef: DatabaseReference,
            callback: (nomeAvversario: String) -> Unit
        ) {
            giocatoriRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(giocatori: DataSnapshot) {

                    val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
                    var nomeAvversario = "non hai un avversario"

                    for (giocatore in giocatori.children) {

                        var giocatore1 = giocatore.key.toString()


                            if (giocatore1 != uid) {
                                nomeAvversario = giocatore.child("name").value.toString()
                                }
                        }


                    callback(nomeAvversario)
                }


                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }





    }





}