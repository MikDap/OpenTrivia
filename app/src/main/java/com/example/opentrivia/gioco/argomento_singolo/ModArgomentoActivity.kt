package com.example.opentrivia.gioco.argomento_singolo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.opentrivia.CaricamentoFragment
import com.example.opentrivia.R
import com.example.opentrivia.api.ChiamataApi
import com.example.opentrivia.utils.DatabaseUtils
import com.example.opentrivia.utils.GiocoUtils
import com.google.firebase.database.FirebaseDatabase


class ModArgomentoActivity : AppCompatActivity(), ArgomentoSingoloFragment.MyFragmentListener,
    ChiamataApi.TriviaQuestionCallback {

    private lateinit var partita: String
    private lateinit var difficolta: String
    lateinit var avversario: String
    lateinit var avversarioNome: String
    lateinit var topic: String
    lateinit var categoria: String
    lateinit var sfidaAccettata: String

    private lateinit var chiamataApi: ChiamataApi
    private var database = FirebaseDatabase.getInstance()

    var domanda: String = ""
    var rispostaCorretta: String = ""
    var listaRisposte = mutableListOf<String>()


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


           //SE QUESTA ACTIVITY è STATA CHIAMATA DA SFIDAFRAGMENT, CHIAMIAMO FRAGMENT CARICAMENTO(SENNO FA RISCEGLIERE TOPIC)
        if (sfidaAccettata == "true"){
            val caricamentoFragment = CaricamentoFragment()
       supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerViewGioco2, caricamentoFragment).commit()
            categoria = GiocoUtils.getCategoria(topic)
           getTriviaQuestion()
        }
    }


    // QUANDO SCEGLIAMO TOPIC SU ARGOMENTOSINGOLO FRAGMENT (SOSTITUITO DA CARICAMENTOFRAGMENT SE HAI ACCETTATO SFIDA)
    override fun onVariablePassed(topic: String) {

        Log.d("onVariablePassed","si")
        //salviamo il topic
        this.topic = topic

//chiamiamo la funzione per ottenere il numero delle categorie per il topic selezionato
        categoria = GiocoUtils.getCategoria(topic)

        creaPartitaDatabase()

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

        Log.d("ontriviaqQuestion","si")
// PASSIAMO AL PROSSIMO FRAGMENT SCELTAMULTIPLA
        val sceltaMultiplaFragmentArgSingolo = SceltaMultiplaFragmentArgSingolo()
        sceltaMultiplaFragmentArgSingolo.setParametriPartita(partita, "argomento singolo", difficolta, topic)
        // getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, secondFragment).addToBackStack(null).commit();
        Handler(Looper.getMainLooper()).postDelayed({
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerViewGioco2, sceltaMultiplaFragmentArgSingolo).commit()
        }, 2000)

    }


    //CHIAMATA API PER DOMANDA E RISPOSTE
    fun getTriviaQuestion() {
        chiamataApi = ChiamataApi("multiple", categoria, difficolta)
        chiamataApi.fetchTriviaQuestion(this)
        Log.d("getTriviaQuestion", "si")

    }



    //SE TROVA UNA PARTITA ASSOCIA L'UTENTE SENNO CREA UNA PARTITA
    fun creaPartitaDatabase() {

        val partiteRef = database.getReference("partite").child("argomento singolo").child(difficolta)

        if (avversario == "casuale") {
            //SE POSSO ASSOCIO L'UTENTE A UNA PARTITA
            DatabaseUtils.associaPartita("argomento singolo", difficolta, topic) { associato, partita ->
                if (associato) {
                    this.partita = partita
                }
                //ALTRIMENTI CREO UNA PARTITA
                else {
                    DatabaseUtils.creaPartita("argomento singolo", partiteRef, topic) { partita ->
                        this.partita = partita
                    }
                }
            }
        }
        //HAI SFIDATO UN AMICO
        else {
            DatabaseUtils.sfidaAmico("argomento singolo", difficolta, topic, avversario, avversarioNome){ partita ->
                this.partita = partita
            }
        }

    }

}



