package com.example.opentrivia
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class StatisticheFragment {
    companion object {
        private final lateinit var database: FirebaseDatabase

        fun StatisticheTerminate(
            partita: String,
            modalita: String,
            difficolta: String,
            utente: String,
            risposte1: Int,
            risposte2: Int

        ) {
            database = FirebaseDatabase.getInstance()
            var uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
            val refToCopy =
                database.getReference("partite").child(modalita).child(difficolta).child(partita)
// Aggiungi un listener per l'evento di valore per leggere i dati dal nodo di origine
            refToCopy.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val dataToCopy = dataSnapshot.getValue() // Leggi i dati dal nodo di origine
                        val refToNewNode =
                            database.getReference("users").child(utente).child("partite terminate")
                                .child(modalita).child(difficolta).child(partita)
                        // Scrivi i dati nel nuovo nodo
                        refToNewNode.setValue(dataToCopy).addOnCompleteListener { task ->
                            if (task.isSuccessful) {


                                refToCopy.removeValue()

                                if(modalita.equals("classica")){
                                    database.getReference("users").child(uid).child("partite in corso").child(partita).removeValue()
                                }
                                Log.e("FirebaseCopy", "SUCCESSO")
                                // I dati sono stati copiati con successo
                                val refToNewNode =
                                    database.getReference("users").child(utente).child("partite terminate")
                                        .child(modalita).child(difficolta).child(partita)
                                if (utente == uid) {
                                    refToNewNode.child("esito").child("io").setValue(risposte1)
                                    refToNewNode.child("esito").child("avversario").setValue(risposte2)
                                }
                                else {
                                    refToNewNode.child("esito").child("io").setValue(risposte2)
                                    refToNewNode.child("esito").child("avversario").setValue(risposte1)
                                }


                            } else {
                                Log.e("FirebaseCopy", "Errori scrittura")
                                // Gestisci errori di scrittura qui
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Gestisci eventuali errori durante la lettura dei dati
                }
            })


        }






        fun updateStatTopic(
            topic: String,
            tipo: String
        ) {
            database = FirebaseDatabase.getInstance()
            var uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
            var statRef = database.getReference("users").child(uid).child("stat")
            statRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(statistiche: DataSnapshot) {


                    if (statistiche.child(topic).hasChild("risposteTotali")) {

                            var risposteCorretteDatabase = 0F

                            if (statistiche.child(topic).hasChild("risposteCorrette")) {

                                risposteCorretteDatabase = statistiche.child(topic).child("risposteCorrette").value.toString().toFloat()

                            }

                        var risposteCorretteAggiornate = risposteCorretteDatabase
                        if (tipo == "corretta") {

                            risposteCorretteAggiornate += 1

                        }

                        var risposteTotaliDatabase = statistiche.child(topic).child("risposteTotali").value.toString().toFloat()


                        var risposteTotaliAggiornate = risposteTotaliDatabase +1

                        //aggiorno la percentuale delle risposte corrette
                        var risposteCorrettePercentuale =
                            (risposteCorretteAggiornate * 100 / risposteTotaliAggiornate)

                        //e la setto sul database
                        statRef.child(topic).child("%risposteCorrette").setValue(risposteCorrettePercentuale)
                        statRef.child(topic).child("risposteCorrette").setValue(risposteCorretteAggiornate)
                        statRef.child(topic).child("risposteTotali").setValue(risposteTotaliAggiornate)
                    }
                    else {
                        var risposteCorretteAggiornate = 0
                          if (tipo == "corretta") {
                           risposteCorretteAggiornate = 1
                           }

                        var risposteTotaliAggiornate = 1

                        //aggiorno la percentuale delle risposte corrette
                        var risposteCorrettePercentuale =
                            (risposteCorretteAggiornate * 100 / risposteTotaliAggiornate)

                        //e  setto sul database
                        statRef.child(topic).child("%risposteCorrette").setValue(risposteCorrettePercentuale)
                        statRef.child(topic).child("risposteCorrette").setValue(risposteCorretteAggiornate)
                        statRef.child(topic).child("risposteTotali").setValue(risposteTotaliAggiornate)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }


    }
}