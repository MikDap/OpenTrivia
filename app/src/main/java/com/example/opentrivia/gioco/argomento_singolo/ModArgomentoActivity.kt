package com.example.opentrivia.gioco.argomento_singolo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.opentrivia.CaricamentoFragment
import com.example.opentrivia.R
import com.example.opentrivia.api.ChiamataApi
import com.example.opentrivia.utils.GiocoUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Random


class ModArgomentoActivity : AppCompatActivity(), ArgomentoSingoloFragment.MyFragmentListener,
    ChiamataApi.TriviaQuestionCallback {

    private lateinit var partita: String
    private lateinit var difficolta: String
    lateinit var avversario: String
    lateinit var avversarioNome: String
    private lateinit var chiamataApi: ChiamataApi
    private lateinit var database: FirebaseDatabase
    var domanda: String = ""
    var rispostaCorretta: String = ""
    var listaRisposte = mutableListOf<String>()
    lateinit var topic: String
    lateinit var categoria: String
    private lateinit var inAttesa: String
    private lateinit var sfidaAccettata: String
    var contatoreRisposte = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mod_argomento_activity)


        //prendiamo i parametri passati da IniziaPartita o da SfidaFragment
        difficolta = intent.getStringExtra("difficolta") ?: ""
        avversario = intent.getStringExtra("avversario").toString()
        avversarioNome = intent.getStringExtra("avversarioNome").toString()
        sfidaAccettata = intent.getBooleanExtra("sfidaAccettata", false).toString()
        partita = intent.getStringExtra("partita") ?: ""
        topic = intent.getStringExtra("topic") ?: ""

        invalidateOptionsMenu()


        if (sfidaAccettata == "true"){
            val secondFragment = CaricamentoFragment()
       supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerViewGioco2, secondFragment).commit()
            categoria = GiocoUtils.getCategoria(topic)
           getTriviaQuestion()
        }
    }


    // quando viene scelto il topic sul ArgomentoSingoloFragment:
    override fun onVariablePassed(topic: String) {

        //salviamo il topic
        this.topic = topic

//chiamiamo la funzione per ottenere il numero delle categorie per il topic selezionato
        categoria = GiocoUtils.getCategoria(topic)



        if (sfidaAccettata == "false") {
            // Utilizza la variabile passata dal fragment
            creaPartitaDatabase()
        } else {
            GiocoUtils.sfidaAccettata(partita, "argomento singolo", difficolta)
        }

        //facciamo la chiamata api
        chiamataApi = ChiamataApi("multiple", categoria, difficolta)
        chiamataApi.fetchTriviaQuestion(this)

    }


    //quando chiamataApi termina (callback)
    override fun onTriviaQuestionFetched(
        tipo: String,
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

        listaRisposte.shuffle()

// passiamo al fragment sceltaMultipla
        val secondFragment = SceltaMultiplaFragmentArgSingolo()
        secondFragment.setParametriPartita(partita, "argomento singolo", difficolta, topic)
        // getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, secondFragment).addToBackStack(null).commit();
        Handler(Looper.getMainLooper()).postDelayed({
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerViewGioco2, secondFragment).commit()
        }, 2000)

    }




    //da commentare
    fun getTriviaQuestion() {
        chiamataApi = ChiamataApi("multiple", categoria, difficolta)
        chiamataApi.fetchTriviaQuestion(this)
        Log.d("getTriviaQuestion", "si")

    }




    fun creaPartitaDatabase() {

// prendiamo l'istanza del database (ci serve per creare sul database la partita)
        database = FirebaseDatabase.getInstance()
        // partiteRef = database/partite/modalita/difficolta
        val partiteRef =
            database.getReference("partite").child("argomento singolo").child(difficolta)


        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val name = FirebaseAuth.getInstance().currentUser?.displayName.toString()




        if (avversario == "casuale") {

            var condizioneSoddisfatta = false

//Listener per database/partite/modalita/difficolta
            partiteRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {


/// Se database/partite/modalita/difficolta ha almeno una partita(con qualcuno in attesa):
                    if (dataSnapshot.hasChildren()) {
                        for (sottonodo in dataSnapshot.children) {
                            //se c'è almeno una partita con un giocatore in attesa..(lo associa)
                            var giocatorediverso = true
                            if (sottonodo.child("giocatori").hasChild(uid)) {
                                giocatorediverso = false
                            }
                            if (sottonodo.child("inAttesa").value == "si" && sottonodo.hasChild("topic") && giocatorediverso) {
                                if (sottonodo.child("topic").value == topic) {
                                    //prende id della partita
                                    partita = sottonodo.key.toString()
                                    //setta database/partite/modalita/difficolta/giocatori/id
                                    partiteRef.child(partita).child("giocatori").child(uid)
                                        .child("name").setValue(name)
                                    //cambia inAttesa in no
                                    partiteRef.child(partita).child("inAttesa").setValue("no")
                                    condizioneSoddisfatta = true

                                    break
                                }
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
                        partiteRef.child(partita).child("giocatori")
                            .child(uid).child("name").setValue(name)
                        condizioneSoddisfatta = true

                    }


                    //           Se database/partite/modalita/difficolta ha almeno una partita ma nessuno in Attesa
                    if (!condizioneSoddisfatta) {
                        partita = partiteRef.push().key.toString()
                        inAttesa = "si"
                        partiteRef.child(partita).child("inAttesa")
                            .setValue(inAttesa)
                        partiteRef.child(partita).child("topic")
                            .setValue(topic)
                        partiteRef.child(partita).child("giocatori")
                            .child(uid).child("name").setValue(name)
                        //   partiteRef.child(partita).child("giocatori").child(uid).child(name).child("risposteCorrette").setValue(0)
                        condizioneSoddisfatta = true
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        } else {

            Log.d("giusto","si")
            //HAI SFIDATO UN AMICO
            partita = partiteRef.push().key.toString()
            inAttesa = "no"
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

            sfideAmicoRef.child(partita).child("modalita").setValue("argomento singolo")
            sfideAmicoRef.child(partita).child("difficolta").setValue(difficolta)
            sfideAmicoRef.child(partita).child("avversarioID").setValue(uid)
            sfideAmicoRef.child(partita).child("avversario").setValue(name)
            sfideAmicoRef.child(partita).child("topic").setValue(topic)

        }


    }


}



