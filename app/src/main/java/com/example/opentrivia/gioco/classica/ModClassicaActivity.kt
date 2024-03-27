package com.example.opentrivia.gioco.classica

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.opentrivia.R
import com.example.opentrivia.api.ChiamataApi
import com.example.opentrivia.utils.DatabaseUtils
import com.example.opentrivia.utils.GiocoUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ModClassicaActivity : AppCompatActivity(),ChiamataApi.TriviaQuestionCallback {

     var partita: String = ""
     lateinit var difficolta: String
     lateinit var avversario: String
    private lateinit var chiamataApi: ChiamataApi
    private var database = FirebaseDatabase.getInstance()
    var domanda : String = ""
    var rispostaCorretta : String = ""

    var listaRisposte = mutableListOf<String>()
    lateinit var topic: String
    lateinit var categoria: String
    private lateinit var sfidaAccettata: String
    private lateinit var avversarioNome: String



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
        avversarioNome = intent.getStringExtra("avversarioNome").toString()
        sfidaAccettata = intent.getBooleanExtra("sfidaAccettata", false).toString()

     invalidateOptionsMenu()
    }


// quando finisce di girare la ruota in RuotaFragment e viene scelto il topic:
    fun passTopicToActivity(topic: String) {

        //salviamo il topic
        this.topic = topic

        // Controlla se partita non è stata inizializzata
    if (sfidaAccettata == "false") {
        if (partita == "") { creaPartitaDatabase() }
    }
    else {
        DatabaseUtils.sfidaAccettata(partita, "classica", difficolta)
    }

if (!jolly()) {
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

        Handler(Looper.getMainLooper()).postDelayed({
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerViewGioco, RuotaFragment).commit()
        }, 100)

    }


    fun chiamaConquista() {
        val conquistaFragment = ModClassicaConquistaFragment()
        conquistaFragment.setDifficolta(partita, difficolta)

        Handler(Looper.getMainLooper()).postDelayed({
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerViewGioco, conquistaFragment).commit()
        }, 100)

    }


//SE TROVA UNA PARTITA ASSOCIA L'UTENTE SENNO CREA UNA PARTITA
    fun creaPartitaDatabase() {

        val partiteRef = database.getReference("partite").child("classica").child(difficolta)

    if (avversario == "casuale") {
        //SE POSSO ASSOCIO L'UTENTE A UNA PARTITA
        DatabaseUtils.associaPartita("classica", difficolta, "nessuno") { associato, partita ->
            if (associato) {
                this.partita = partita
            }
            //ALTRIMENTI CREO UNA PARTITA
            else {
                DatabaseUtils.creaPartita("classica", partiteRef, topic) { partita ->
                    this.partita = partita
                }
            }
        }
    }
    //HAI SFIDATO UN AMICO
    else {
        DatabaseUtils.sfidaAmico("classica", difficolta, topic, avversario, avversarioNome){ partita ->
            this.partita = partita
        }
    }

    }

    fun chiamaConquistaSceltaMultipla(topic:String, domanda:String, risposta1:String, risposta2:String, risposta3:String, risposta4:String, rispostaCorretta:String) {

        domandaConquista = domanda
        risposta1Conquista = risposta1
        risposta2Conquista = risposta2
        risposta3Conquista = risposta3
        risposta4Conquista = risposta4
        rispostaCorrettaConquista = rispostaCorretta
        topicConquista = topic

        // passiamo al secondo Fragment (DA GESTIRE IL PERMESSO DI RITORNARE INDIETRO DURANTE LA SCHERMATA DELLE DOMANDE E RISPOSTE)
        val secondFragment = ConquistaSceltaMultiplaFragment()

        // getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, secondFragment).addToBackStack(null).commit();
        Handler(Looper.getMainLooper()).postDelayed({
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerViewGioco, secondFragment).commit()
        }, 500)

    }


    fun jolly(): Boolean {
        if (topic == "jolly") {
            chiamaConquista()
            val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
            database = FirebaseDatabase.getInstance()
            val giocatoreRef =
                database.getReference("partite").child("classica").child(difficolta).child(partita)
                    .child("giocatori").child(uid)
            giocatoreRef.child("risposteTotCorrette").setValue(0)
            return true
        } else {
            return false }
    }

    fun getTriviaQuestions() {
//chiamiamo la funzione per ottenere il numero delle categorie per il topic selezionato
            categoria = GiocoUtils.getCategoria(topic)
            //facciamo la chiamata api
            chiamataApi = ChiamataApi("multiple", categoria, difficolta)
            chiamataApi.fetchTriviaQuestion(this)
    }


}