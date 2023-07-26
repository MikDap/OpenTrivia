package com.example.opentrivia.gioco

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.opentrivia.R
import com.example.opentrivia.api.ChiamataApi
import com.google.firebase.database.FirebaseDatabase
import java.util.Random


class ModArgomentoActivity : AppCompatActivity(), ArgomentoSingoloFragment.MyFragmentListener, ChiamataApi.TriviaQuestionCallback
{

    private lateinit var partita: String
    private lateinit var difficolta: String
    private lateinit var chiamataApi: ChiamataApi
    private lateinit var database: FirebaseDatabase
     var domanda : String = ""
    var rispostaCorretta : String = ""
     var risposta1: String = ""
     var risposta2: String = ""
     var risposta3: String = ""
     var risposta4: String = ""
     var risposte = arrayOf(risposta1, risposta2, risposta3,risposta4)
    lateinit var topic: String
    lateinit var categoria: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mod_argomento)


        //prendiamo i parametri passati dalla SceltaGiocatore
        partita = intent.getStringExtra("partita") ?: ""
        difficolta = intent.getStringExtra("difficolta") ?: ""




    }
// quando viene scelto il topic sul fragment:
    override fun onVariablePassed(topic: String) {
        // Utilizza la variabile passata dal fragment come desiderato

    //salviamo il topic
        this.topic = topic
//chiamiamo la funzione per ottenere il numero delle categorie per il topic selezionato
        categoria = getCategoria(topic)



    database = FirebaseDatabase.getInstance()
    // partiteRef = database/partite/argomento singolo/difficolta
    val partiteRef = database.getReference("partite").child("argomento singolo").child(difficolta).child(partita)
partiteRef.child("topic").setValue(topic)



    //facciamo la chiamata api
        chiamataApi = ChiamataApi(categoria,difficolta)
        chiamataApi.fetchTriviaQuestion(this)

    }


    //quando chiamataApi termina (callback)
    override fun onTriviaQuestionFetched(
        domanda: String,
        risposta_corretta: String,
        risposta_sbagliata_1: String,
        risposta_sbagliata_2: String,
        risposta_sbagliata_3: String
    ) {

        //salviamo la domanda e le risposte
        this.domanda = chiamataApi.domanda
        this.rispostaCorretta = chiamataApi.risposta_corretta
        val risposta_corretta = chiamataApi.risposta_corretta
        val risposta_sbagliata_1 = chiamataApi.risposta_sbagliata_1
        val risposta_sbagliata_2 = chiamataApi.risposta_sbagliata_2
        val risposta_sbagliata_3 = chiamataApi.risposta_sbagliata_3

//vogliamo riordinarle per non far capitare la risposta esatta sempre come prima risposta
//mutable per usare removeAt
        val listaRisposte = mutableListOf(risposta_corretta,risposta_sbagliata_1,risposta_sbagliata_2,risposta_sbagliata_3)

        var i = 0
        while (i <= 3) {
            val randomIndex = Random().nextInt(listaRisposte.size)


            risposte[i] = listaRisposte[randomIndex]

            listaRisposte.removeAt(randomIndex)
            i++
        }
// passiamo al secondo Fragment (DA GESTIRE IL PERMESSO DI RITORNARE INDIETRO DURANTE LA SCHERMATA DELLE DOMANDE E RISPOSTE)
        val secondFragment = SceltaMultiplaFragment()
        secondFragment.setParametriPartita(partita,"argomento singolo", difficolta, topic)
        // getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, secondFragment).addToBackStack(null).commit();
        Handler(Looper.getMainLooper()).postDelayed({
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerViewGioco2, secondFragment).commit()
        }, 2000)

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


    //da commentare
    fun getTriviaQuestion() {
        chiamataApi = ChiamataApi(categoria,difficolta)
        chiamataApi.fetchTriviaQuestion(this)
        Log.d("getTriviaQuestion","si")

    }
}