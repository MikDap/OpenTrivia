package com.example.opentrivia.gioco.classica

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.opentrivia.R
import com.example.opentrivia.api.ChiamataApi
import com.example.opentrivia.utils.GiocoUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ModClassicaActivity : AppCompatActivity(), RuotaFragment.MyFragmentListener,ChiamataApi.TriviaQuestionCallback {

     var partita: String = ""
     lateinit var difficolta: String
     lateinit var avversario: String
    private lateinit var chiamataApi: ChiamataApi
    private lateinit var database: FirebaseDatabase
    var domanda : String = ""
    var rispostaCorretta : String = ""

    var listaRisposte = mutableListOf<String>()
    lateinit var topic: String
    lateinit var categoria: String
    private lateinit var inAttesa: String
    private lateinit var sfidaAccettata: String



    lateinit var topicConquista : String
    lateinit var domandaConquista : String
    lateinit var risposta1Conquista: String
    lateinit var risposta2Conquista: String
    lateinit var risposta3Conquista: String
    lateinit var risposta4Conquista: String
    lateinit var rispostaCorrettaConquista: String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mod_classica_activity)

        difficolta = intent.getStringExtra("difficolta") ?: ""
        partita = intent.getStringExtra("partita") ?:""
        avversario = intent.getStringExtra("avversario").toString()
        sfidaAccettata = intent.getStringExtra("sfidaAccettata").toString()

     invalidateOptionsMenu()
    }


// quando finisce di girare la ruota in RuotaFragment e viene scelto il topic:
    override fun onVariablePassed(topic: String) {

        //salviamo il topic
        this.topic = topic

        // Controlla se partita non è stata inizializzata
    if (sfidaAccettata == "false") {

        if (partita == "") {
            creaPartitaDatabase(){
                getTriviaQuestions()
            }
        }
        else {
            getTriviaQuestions()
        }
    } else {
        GiocoUtils.sfidaAccettata(partita, "classica", difficolta)
        getTriviaQuestions()
    }



    }


    //quando chiamataApi termina (callback)
    override fun onTriviaQuestionFetched(
        tipo:String,
        domanda: String,
        risposta_corretta: String,
        risposta_sbagliata_1: String,
        risposta_sbagliata_2: String,
        risposta_sbagliata_3: String
    ) {

        //salviamo la domanda e le risposte
        this.domanda = chiamataApi.domanda
        this.rispostaCorretta = chiamataApi.risposta_corretta

        listaRisposte.clear()
        listaRisposte.addAll(
            listOf(
                risposta_corretta,
                risposta_sbagliata_1,
                risposta_sbagliata_2,
                risposta_sbagliata_3
            )
        )

        // mescoliamo le risposte
        listaRisposte.shuffle()

        
// passiamo al fragment SceltaMultipla,la domanda e le risposte le prende nell'onCreate
        val secondFragment = SceltaMultiplaFragmentClassica()

        secondFragment.partita = partita
        secondFragment.modalita = "classica"
        secondFragment.difficolta = difficolta
        secondFragment.topic = topic

        // getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, secondFragment).addToBackStack(null).commit();
        Handler(Looper.getMainLooper()).postDelayed({
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerViewGioco, secondFragment).commit()
        }, 500)
    }




    fun chiamaRuota() {
        val RuotaFragment = RuotaFragment()

 //       RuotaFragment.setParametriPartita(partita, "classica", difficolta,topic)
        // getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, secondFragment).addToBackStack(null).commit();
        Handler(Looper.getMainLooper()).postDelayed({
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerViewGioco, RuotaFragment).commit()
        }, 100)

    }


    fun chiamaConquista() {
        val conquistaFragment = mod_classica_conquista()
        conquistaFragment.setDifficolta(partita, difficolta)

        // getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, secondFragment).addToBackStack(null).commit();
        Handler(Looper.getMainLooper()).postDelayed({
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerViewGioco, conquistaFragment).commit()
        }, 100)

    }


    fun creaPartitaDatabase(callback: () -> Unit) {

        database = FirebaseDatabase.getInstance()
        // partiteRef = database/partite/modalita/difficolta
        val partiteRef = database.getReference("partite").child("classica").child(difficolta)


        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val name = FirebaseAuth.getInstance().currentUser?.displayName.toString()

        var condizioneSoddisfatta = false


        partiteRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(listaPartite: DataSnapshot) {


           // Se partiteRef ha almeno una partita(con qualcuno in attesa):
                if (listaPartite.hasChildren()) {
                    for (partita1 in listaPartite.children) {


                        var giocatorediverso = true
                        if (partita1.child("giocatori").hasChild(uid)) {
                            giocatorediverso = false
                        }

                        var haFinitoTurno = false
                        val turno : String =  partita1.child("Turno").value.toString()
                        if (turno == "-") {
                            haFinitoTurno = true
                        }

                        //se c'è almeno una partita, con un giocatore in attesa e ha finito il turno, lo associa
                        if (partita1.child("inAttesa").value == "si" && giocatorediverso && haFinitoTurno) {

                            //prende id della partita
                            partita = partita1.key.toString()
                            //setta database/partite/modalita/difficolta/giocatori/id
                            partiteRef.child(partita).child("giocatori").child(uid).child("name").setValue(name)
                            Log.d("name",name)
                            //cambia inAttesa in no
                            partiteRef.child(partita).child("inAttesa").setValue("no")

                            condizioneSoddisfatta = true

                            break
                        }
                    }



/// se database/partite/modalita/difficolta non ha partite:
                } else {
                    //crea id della partita ecc
                    partita = partiteRef.push().key.toString()
                    inAttesa = "si"
                    partiteRef.child(partita).child("inAttesa")
                        .setValue(inAttesa)
                    partiteRef.child(partita).child("topic")
                        .setValue(topic)
                    partiteRef.child(partita).child("Turno")
                        .setValue(uid)
                    partiteRef.child(partita).child("giocatori").child(uid).child("name").setValue(name)
                    condizioneSoddisfatta = true
                }



                // Se database/partite/modalita/difficolta ha almeno una partita ma nessuno in Attesa
                if (!condizioneSoddisfatta) {
                    partita = partiteRef.push().key.toString()
                    inAttesa = "si"
                    partiteRef.child(partita).child("inAttesa")
                        .setValue(inAttesa)
                    partiteRef.child(partita).child("topic")
                        .setValue(topic)
                    partiteRef.child(partita).child("Turno")
                        .setValue(uid)
                    partiteRef.child(partita).child("giocatori").child(uid).child("name").setValue(name)
                    //   partiteRef.child(partita).child("giocatori").child(uid).child(name).child("risposteCorrette").setValue(0)
                    condizioneSoddisfatta = true
                }

                callback()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun chiamaConquistaSceltaMultipla(topic:String, domanda:String, risposta1:String, risposta2:String, risposta3:String, risposta4:String, rispostaCorretta:String) {

        domandaConquista = domanda
        risposta1Conquista = risposta1
        risposta2Conquista = risposta2
        risposta3Conquista = risposta3
        risposta4Conquista = risposta4
        rispostaCorrettaConquista = rispostaCorretta
        topicConquista = topic

        Log.d("risposta1Conquista", risposta1Conquista)

        // passiamo al secondo Fragment (DA GESTIRE IL PERMESSO DI RITORNARE INDIETRO DURANTE LA SCHERMATA DELLE DOMANDE E RISPOSTE)
        val secondFragment = ConquistaSceltaMultipla()

        Log.d("domandaconqSceltaMultipla", domanda)
        // getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, secondFragment).addToBackStack(null).commit();
        Handler(Looper.getMainLooper()).postDelayed({
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerViewGioco, secondFragment).commit()
        }, 500)

    }





    fun getTriviaQuestions() {


        if (topic == "jolly") {
            chiamaConquista()
            val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
            database = FirebaseDatabase.getInstance()
            val giocatoreRef =
                database.getReference("partite").child("classica").child(difficolta).child(partita)
                    .child("giocatori").child(uid)
            giocatoreRef.child("risposteTotCorrette").setValue(0)

        }
        else {
//chiamiamo la funzione per ottenere il numero delle categorie per il topic selezionato
            categoria = GiocoUtils.getCategoria(topic)


            //facciamo la chiamata api
            chiamataApi = ChiamataApi("multiple", categoria, difficolta)
            chiamataApi.fetchTriviaQuestion(this)
        }
    }


}