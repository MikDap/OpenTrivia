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

            // CONTROLLARE SE DA PROBLEMI VISTO CHE LA PARTE DI SOPRA è ASINCRONA

        }


        // partiteTerminate/modalita/idPartita/
        // esito(lo calcoliamo dopo) - io - avversario/               <- argomenti conquistati
        // topic/
        //  arte../risposte corr-sba-tot
//PROBABILMENTE DOVREMO FAR ESEGUIRE IL CODICE QUANDO VIENE AVVIATO IL GIOCO E
        // FARE QUINDI UNA SCHERMATA DI CARICAMENTO PRIMA DI PASSARE AL MENU PRINCIPALE DEL GIOCO
        //A CAUSA DELLA COMPLESSITA COMPUTAZIONALE
        //NELL'ONCREATE DEL FRAGMENT :
        fun updateStatistiche() {
            database = FirebaseDatabase.getInstance()
            var uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
            var partiteTerminateRef =
                database.getReference("users").child(uid).child("partite terminate")
            partiteTerminateRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(partiteTerminate: DataSnapshot) {
                    for (modalita in partiteTerminate.children) {
                        for (partita in modalita.children) {
                            val uidSnapshot = partita.child(uid)
                            for (topic in uidSnapshot.children) {
                                var topicNome = topic.key.toString()
                                var risposteCorrette =
                                    topic.child("RisposteCorrette").value.toString().toInt()
                                var risposteSbagliate =
                                    topic.child("RisposteSbagliate").value.toString().toInt()
                                var risposteTotali =
                                    topic.child("RisposteTotali").value.toString().toInt()
                                updateStatisticheTopic(
                                    topicNome,
                                    risposteCorrette,
                                    risposteSbagliate,
                                    risposteTotali
                                )
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Gestisci l'errore, se necessario
                }
            })
        }
//      users/uid/stat/topic/
//  /risposte corrette-sbagliate-totali  % risposte corrette

        fun updateStatisticheTopic(
            topic: String,
            risposteCorrette: Int,
            risposteSbagliate: Int,
            risposteTotali: Int
        ) {
            var statRef = database.getReference("users").child("stat")
            statRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(statistiche: DataSnapshot) {
                    var risposteCorretteDatabase =
                        statistiche.child(topic).child("risposteCorrette").value.toString().toInt()
                    var risposteSbagliateDatabase =
                        statistiche.child(topic).child("risposteSbagliate").value.toString().toInt()
                    var risposteTotaliDatabase =
                        statistiche.child(topic).child("risposteTotali").value.toString().toInt()
                    var risposteCorretteAggiornate = risposteCorretteDatabase + risposteCorrette
                    var risposteSbagliateAggiornate = risposteSbagliateDatabase + risposteSbagliate
                    var risposteTotaliAggiornate = risposteTotaliDatabase + risposteTotali
                    //aggiorno la percentuale delle risposte corrette
                    var risposteCorrettePercentuale =
                        (risposteCorretteAggiornate * 100 / risposteTotaliAggiornate)
                    //e la setto sul database
                    statRef.child(topic).child("%risposteCorrette")
                        .setValue(risposteCorrettePercentuale)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }
}