package com.example.opentrivia.utils

import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import androidx.fragment.app.FragmentManager
import com.example.opentrivia.R
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

class GiocoUtils {

    companion object {
        lateinit var database: FirebaseDatabase


        fun schermataAttendi(fragmentManager: FragmentManager, containerId: Int) {
            val fragment = AttendiTurnoFragment()
            Handler(Looper.getMainLooper()).postDelayed({
                fragmentManager.beginTransaction()
                    .replace(containerId, fragment).commit()
            }, 500)
        }


        fun schermataVittoria(
            fragmentManager: FragmentManager,
            containerId: Int,
            nomeAvv: String,
            scoreMio: Int,
            scoreAvv: Int,
            mod: String
        ) {
            val fragment = Vittoria().apply {
                this.nomeAvv = nomeAvv
                this.scoreMio = scoreMio.toString()
                this.scoreAvv = scoreAvv.toString()
                this.mod = mod
            }
            Handler(Looper.getMainLooper()).postDelayed({
                fragmentManager.beginTransaction()
                    .replace(containerId, fragment).commit()
            }, 500)
        }


        fun schermataPareggio(
            fragmentManager: FragmentManager,
            containerId: Int,
            nomeAvv: String,
            scoreMio: Int,
            scoreAvv: Int,
            mod: String
        ) {
            val fragment = Pareggio().apply {
                this.nomeAvv = nomeAvv
                this.scoreMio = scoreMio.toString()
                this.scoreAvv = scoreAvv.toString()
                this.mod = mod
            }
            Handler(Looper.getMainLooper()).postDelayed({
                fragmentManager.beginTransaction()
                    .replace(containerId, fragment).commit()
            }, 500)
        }


        fun schermataSconfitta(
            fragmentManager: FragmentManager,
            containerId: Int,
            nomeAvv: String,
            scoreMio: Int,
            scoreAvv: Int,
            mod: String
        ) {
            val fragment = Sconfitta().apply {
                this.nomeAvv = nomeAvv
                this.scoreMio = scoreMio.toString()
                this.scoreAvv = scoreAvv.toString()
                this.mod = mod
            }
            Handler(Looper.getMainLooper()).postDelayed({
                fragmentManager.beginTransaction()
                    .replace(containerId, fragment).commit()
            }, 500)
        }


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


        // in base al topic ricevuto, restituisco i numeri dedicati al topic (specificati in OpenTriviaDB)
        fun getCategoria(topic: String): String {
            lateinit var categoria: String
            val categorie_culturaPop = arrayOf("9","10","11","12","13","14","15","16","29","31","32")
            val categorie_scienze = arrayOf("17","18","19","30")
            when (topic) {
                "culturaPop" -> { categoria = getRandomTopic(categorie_culturaPop) }
                "sport" -> {categoria = "21"}
                "storia" ->{categoria = "23"}
                "geografia" -> {categoria= "22"}
                "arte" -> {categoria = "25"}
                "scienze" -> {categoria = getRandomTopic(categorie_scienze)}
            }
            return categoria
        }


        //ritorna una categoria random
        fun getRandomTopic(topics: Array<String>): String {
            val randomIndex = Random().nextInt(topics.size)
            return topics[randomIndex]
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





        fun controllaAvversarioERispCorrette(modalita: String, difficolta: String, partita: String) {
            database = FirebaseDatabase.getInstance()
            val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
            var giocatoriRef =
                database.getReference("partite").child(modalita).child(difficolta).child(partita)
                    .child("giocatori")
            var risposte1 = 0
            var risposte2 = 0
            var giocatore2esiste = false
            var avversario = ""
            var nomeAvv = ""

            giocatoriRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (giocatore in dataSnapshot.children) {

                        if(giocatore.key.toString() != uid){
                            giocatore2esiste = true
                            avversario = giocatore.key.toString()
                            nomeAvv = giocatore.child("name").value.toString()
                        }

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
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }




    }
}