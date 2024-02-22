package com.example.opentrivia.utils

import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import androidx.fragment.app.FragmentManager
import com.example.opentrivia.gioco.AttendiTurnoFragment
import com.example.opentrivia.gioco.Pareggio
import com.example.opentrivia.gioco.Sconfitta
import com.example.opentrivia.gioco.Vittoria
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Random

class DatabaseUtils {

    companion object {
       var database = FirebaseDatabase.getInstance()
       var uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
       var name = FirebaseAuth.getInstance().currentUser?.displayName.toString()



        fun spostaInPartiteTerminate(
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

                                if (modalita.equals("classica")) {
                                    database.getReference("users").child(uid)
                                        .child("partite in corso").child(partita).removeValue()
                                }
                                Log.e("FirebaseCopy", "SUCCESSO")

                                // I dati sono stati copiati con successo
                                val refToNewNode =
                                    database.getReference("users").child(utente)
                                        .child("partite terminate")
                                        .child(modalita).child(difficolta).child(partita)
                                if (utente == uid) {
                                    refToNewNode.child("esito").child("io").setValue(risposte1)
                                    refToNewNode.child("esito").child("avversario")
                                        .setValue(risposte2)
                                } else {
                                    refToNewNode.child("esito").child("io").setValue(risposte2)
                                    refToNewNode.child("esito").child("avversario")
                                        .setValue(risposte1)
                                }


                            } else {
                                Log.e("FirebaseCopy", "Errori scrittura")
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

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

                            risposteCorretteDatabase =
                                statistiche.child(topic).child("risposteCorrette").value.toString()
                                    .toFloat()

                        }

                        var risposteCorretteAggiornate = risposteCorretteDatabase
                        if (tipo == "corretta") {

                            risposteCorretteAggiornate += 1

                        }

                        var risposteTotaliDatabase =
                            statistiche.child(topic).child("risposteTotali").value.toString()
                                .toFloat()


                        var risposteTotaliAggiornate = risposteTotaliDatabase + 1

                        //aggiorno la percentuale delle risposte corrette
                        var risposteCorrettePercentuale =
                            (risposteCorretteAggiornate * 100 / risposteTotaliAggiornate)

                        //e la setto sul database
                        statRef.child(topic).child("%risposteCorrette")
                            .setValue(risposteCorrettePercentuale)
                        statRef.child(topic).child("risposteCorrette")
                            .setValue(risposteCorretteAggiornate)
                        statRef.child(topic).child("risposteTotali")
                            .setValue(risposteTotaliAggiornate)
                    } else {
                        var risposteCorretteAggiornate = 0
                        if (tipo == "corretta") {
                            risposteCorretteAggiornate = 1
                        }

                        var risposteTotaliAggiornate = 1

                        //aggiorno la percentuale delle risposte corrette
                        var risposteCorrettePercentuale =
                            (risposteCorretteAggiornate * 100 / risposteTotaliAggiornate)

                        //e  setto sul database
                        statRef.child(topic).child("%risposteCorrette")
                            .setValue(risposteCorrettePercentuale)
                        statRef.child(topic).child("risposteCorrette")
                            .setValue(risposteCorretteAggiornate)
                        statRef.child(topic).child("risposteTotali")
                            .setValue(risposteTotaliAggiornate)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }


        fun sfidaAccettata(partita: String, modalita: String, difficolta: String) {
            database = FirebaseDatabase.getInstance()
            // partiteRef = database/partite/modalita/difficolta
            val partiteRef =
                database.getReference("partite").child(modalita).child(difficolta)


            val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
            val name = FirebaseAuth.getInstance().currentUser?.displayName.toString()

            partiteRef.child(partita).child("giocatori").child(uid)
                .child("name").setValue(name)
            //cambia inAttesa in no
            partiteRef.child(partita).child("inAttesa").setValue("no")

            //  uid/accettato: si
            partiteRef.child(partita).child("giocatori").child(uid).child("accettato").setValue("si")
        }


        //verifa se questa è la risposta corretta, restituisce true o false

        fun QuestaèLaRispostaCorretta(risposta: Button, rispostaCorretta: String): Boolean {


            if (risposta.text == rispostaCorretta) {
                risposta.setBackgroundColor(Color.LTGRAY)
                Handler(Looper.getMainLooper()).postDelayed({
                    risposta.setBackgroundColor(Color.GREEN)
                }, 500)
                return true
            } else {
                risposta.setBackgroundColor(Color.LTGRAY)
                Handler(Looper.getMainLooper()).postDelayed({
                    risposta.setBackgroundColor(Color.RED)
                }, 500)
                return false
            }
        }



        // tipo: risposteCorrette o risposteSbagliate
        fun updateRisposte(
            risposteRef: DatabaseReference, tipo: String
        ) {
            risposteRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(risposte: DataSnapshot) {

                    // Se risposte/corr V sbagl.. esiste nel database aumentiamo i punti di 1
                    if (risposte.child(tipo).exists()) {
                        var punti = risposte.child(tipo).value.toString().toInt()
                        punti++
                        risposteRef.child(tipo).setValue(punti)
                    }
                    //sennò settiamo a 1
                    else {
                        risposteRef.child(tipo).setValue(1)
                    }
                    // Se risposte/risposte totali esiste nel database aumentiamo le risposte totali di 1
                    if (risposte.child("risposteTotali").exists()) {
                        var punti = risposte.child("risposteTotali").value.toString().toInt()
                        punti++
                        risposteRef.child("risposteTotali").setValue(punti)
                    }
                    //sennò le settiamo a 1
                    else {
                        risposteRef.child("risposteTotali").setValue(1)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }




      fun getAvversario(modalita: String, difficolta: String, partita: String, callback: (giocatore2esiste: Boolean, avversario: String, nomeAvv: String) -> Unit){

          database = FirebaseDatabase.getInstance()
          val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
          var giocatoriRef =
              database.getReference("partite").child(modalita).child(difficolta).child(partita)
                  .child("giocatori")
          var giocatore2esiste = false
          var avversario = "-"
          var nomeAvv = "-"

          giocatoriRef.addListenerForSingleValueEvent(object : ValueEventListener {
              override fun onDataChange(listaGiocatori: DataSnapshot) {
                  for (giocatore in listaGiocatori.children) {

                      if(giocatore.key.toString() != uid){
                          giocatore2esiste = true
                          avversario = giocatore.key.toString()
                          nomeAvv = giocatore.child("name").value.toString()
                      }
                  }
                  callback(giocatore2esiste, avversario, nomeAvv)
              }
              override fun onCancelled(error: DatabaseError) {
                  TODO("Not yet implemented")
              }
          })
      }



        fun getRispCorrette(modalita: String, difficolta: String, partita: String, callback: (risposte1: Int, risposte2: Int) -> Unit) {
            database = FirebaseDatabase.getInstance()
            val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
            var giocatoriRef =
                database.getReference("partite").child(modalita).child(difficolta).child(partita)
                    .child("giocatori")
            var risposte1 = 0
            var risposte2 = 0

            giocatoriRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (giocatore in dataSnapshot.children) {

                        // A ENTRAMBI GIOCATORI, PER OGNI TOPIC PRENDO RISPOSTE CORRETTE
                        for (topic in giocatore.children) {

                            if (topic.hasChild("risposteTotali")) {
                                var giocatore1 = giocatore.key.toString()

                                // PER ME
                                if (giocatore1 == uid) {
                                    val risposteCorrette =
                                        topic.child("risposteCorrette").getValue(Int::class.java)
                                    if (risposteCorrette != null) {
                                        risposte1 += risposteCorrette
                                    }
                                }
                                // PER AVVERSARIO
                                else {
                                    val risposteCorrette =
                                        topic.child("risposteCorrette").getValue(Int::class.java)
                                    if (risposteCorrette != null) {
                                        risposte2 += risposteCorrette
                                    }
                                }
                            }
                        }
                    }
                    callback(risposte1, risposte2)
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }





        fun creaPartita(modalita: String, partiteRef: DatabaseReference, topic: String, callback: (partita: String) -> Unit){

            Log.d("creaPart","si")
            //crea id della partita ecc
           val  partita = partiteRef.push().key.toString()
            val inAttesa = "si"

            partiteRef.child(partita).child("inAttesa")
                .setValue(inAttesa)

            partiteRef.child(partita).child("topic")
                .setValue(topic)

            if (modalita == "classica") {
                partiteRef.child(partita).child("Turno")
                    .setValue(uid)
            }

            partiteRef.child(partita).child("giocatori").child(uid).child("name").setValue(name)

            callback(partita)
        }



        fun associaPartita(modalita: String, difficolta: String,topic: String, callback: (associato: Boolean, partita: String) -> Unit){

            val partiteRef = database.getReference("partite").child(modalita).child(difficolta)

            partiteRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(listaPartite: DataSnapshot) {

                    var associato = false
                    var partita = "nessuna"

                    // Se partiteRef ha almeno una partita:
                    if (listaPartite.hasChildren()) {
                        //per ogni partita
                        for (partita1 in listaPartite.children) {

                            //controllo se c'è un giocatore diverso da me
                            var giocatorediverso = true
                            var idAvversario = "-"

                            for(giocatore in partita1.child("giocatori").children){

                                if (giocatore.key.toString() == uid){
                                    giocatorediverso = false
                                }
                                else{
                                    idAvversario = giocatore.key.toString()
                                }
                            }


                            if (modalita == "classica" || modalita == "a tempo") {
                                //controllo se ha finito il turno
                                var haFinitoTurno = false

                                if (modalita == "classica") {
                                    val turno: String = partita1.child("Turno").value.toString()
                                    if (turno == "-") {
                                        haFinitoTurno = true
                                    }
                                }
                                else{

                                    if (giocatorediverso && partita1.child("giocatori").child(idAvversario).hasChild("fineTurno")){
                                        haFinitoTurno = true
                                    }
                                }


                               //se c'è almeno una partita, con un giocatore (diverso da me) in attesa e ha finito il turno, lo associa
                               if (partita1.child("inAttesa").value == "si" && giocatorediverso && haFinitoTurno) {

                                //prende id della partita
                                partita = partita1.key.toString()
                                //setta id
                                partiteRef.child(partita).child("giocatori").child(uid)
                                    .child("name").setValue(name)
                                //cambia inAttesa in no

                                partiteRef.child(partita).child("inAttesa").setValue("no")
                                associato = true

                                break
                               }
                            }
                            else if (modalita == "argomento singolo"){
                                //controllo se ha finito il turno
                                var haFinitoTurno = false

                                if (giocatorediverso && partita1.child("giocatori").child(idAvversario).hasChild("fineTurno")){
                                    haFinitoTurno = true
                                }

                                if (partita1.child("inAttesa").value == "si" && partita1.hasChild("topic") && giocatorediverso && haFinitoTurno) {
                                    if (partita1.child("topic").value == topic) {
                                        //prende id della partita
                                        partita = partita1.key.toString()
                                        //setta database/partite/modalita/difficolta/giocatori/id
                                        partiteRef.child(partita).child("giocatori").child(uid)
                                            .child("name").setValue(name)
                                        //cambia inAttesa in no
                                        partiteRef.child(partita).child("inAttesa").setValue("no")
                                        associato = true
                                        break
                                    }
                                }
                            }



                        }
                    }
                    callback(associato,partita)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }







        fun sfidaAmico(modalita: String, difficolta: String,topic: String, avversario: String, avversarioNome: String, callback: (partita: String) -> Unit){
            //HAI SFIDATO UN AMICO
            val partiteRef =
                database.getReference("partite").child(modalita).child(difficolta)

            val partita = partiteRef.push().key.toString()
            val inAttesa = "no"
            partiteRef.child(partita).child("inAttesa")
                .setValue(inAttesa)
            partiteRef.child(partita).child("topic")
                .setValue(topic)
            partiteRef.child(partita).child("giocatori")
                .child(uid).child("name").setValue(name)
            partiteRef.child(partita).child("sfida")
                .child(avversario).child("name").setValue(avversarioNome)

            partiteRef.child(partita).child("sfida").child(avversario).child("accettato")
                .setValue("in attesa")


            //NOTIFICARE AMICO

            val sfideAmicoRef =
                database.getReference("users").child(avversario).child("sfide")

            sfideAmicoRef.child(partita).child("modalita").setValue(modalita)
            sfideAmicoRef.child(partita).child("difficolta").setValue(difficolta)
            sfideAmicoRef.child(partita).child("avversarioID").setValue(uid)
            sfideAmicoRef.child(partita).child("avversario").setValue(name)
            sfideAmicoRef.child(partita).child("topic").setValue(topic)

            callback(partita)
        }



    }
}