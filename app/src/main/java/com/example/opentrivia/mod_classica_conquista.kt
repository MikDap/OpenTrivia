package com.example.opentrivia

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import com.example.opentrivia.api.ChiamataApi
import com.example.opentrivia.gioco.ModClassicaActivity
import com.example.opentrivia.gioco.SceltaMultiplaFragmentClassica
import java.util.Random


class mod_classica_conquista : Fragment(), ChiamataApi.TriviaQuestionCallback {

    private lateinit var chiamataApi: ChiamataApi
    lateinit var categoria: String
    private lateinit var partita: String
    private lateinit var difficolta: String
    private lateinit var storiaButton: ImageButton
    private lateinit var geografiaButton: ImageButton
    private lateinit var arteButton: ImageButton
    private lateinit var sportButton: ImageButton
    private lateinit var intrattenimentoButton: ImageButton
    private lateinit var scienzeButton: ImageButton
    var domanda : String = ""
    var rispostaCorretta : String = ""
    var risposta1: String = ""
    var risposta2: String = ""
    var risposta3: String = ""
    var risposta4: String = ""
    var risposte = arrayOf(risposta1, risposta2, risposta3, risposta4)
    private lateinit var modClassicaActivity: ModClassicaActivity
    lateinit var topic: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.mod_classica_conquista, container, false)


        modClassicaActivity = activity as ModClassicaActivity
        storiaButton = view.findViewById(R.id.storiabutton)
        geografiaButton = view.findViewById(R.id.geografiabutton)
        arteButton = view.findViewById(R.id.artebutton)
        sportButton = view.findViewById(R.id.sportbutton)
        intrattenimentoButton = view.findViewById(R.id.intrattenimentobutton)
        scienzeButton = view.findViewById(R.id.scienzebutton)

        storiaButton.setOnClickListener {
            getTriviaQuestion("storia")
            topic = "storia"
        }
        geografiaButton.setOnClickListener {
            getTriviaQuestion("geografia")
            topic = "geografia"
        }
        arteButton.setOnClickListener {
            getTriviaQuestion("arte")
            topic = "arte"
        }
        sportButton.setOnClickListener {
            getTriviaQuestion("sport")
            topic = "sport"
        }
        intrattenimentoButton.setOnClickListener {
            getTriviaQuestion("culturaPop")
            topic = "culturaPop"

        }
        scienzeButton.setOnClickListener {
            getTriviaQuestion("scienze")
            topic = "scienze"

        }

        return view
    }


    fun getCategoria(topic: String): String {
        lateinit var categoria: String
        val categorie_culturaPop = arrayOf("9","10","11","12","13","14","15","16","29","31","32")
        val categorie_scienze = arrayOf("17","18","19","30")

        when (topic) {

            "culturaPop" -> { categoria = getRandomCategoria(categorie_culturaPop) }

            "sport" -> {categoria = "21"}

            "storia" ->{categoria = "23"}

            "geografia" -> {categoria= "22"}

            "arte" -> {categoria = "25"}

            "scienze" -> {categoria = getRandomCategoria(categorie_scienze)}

        }
        return categoria
    }

    fun getRandomCategoria(topics: Array<String>): String {
        val randomIndex = Random().nextInt(topics.size)
        return topics[randomIndex]
    }
    fun getTriviaQuestion(topic: String) {
//chiamiamo la funzione per ottenere il numero delle categorie per il topic selezionato
        categoria = getCategoria(topic)
        chiamataApi = ChiamataApi("multiple",categoria,difficolta)
        chiamataApi.fetchTriviaQuestion(this)
        Log.d("getTriviaQuestion","siii")

    }

    override fun onTriviaQuestionFetched(
        tipo:String,
        domanda: String,
        risposta_corretta: String,
        risposta_sbagliata_1: String,
        risposta_sbagliata_2:String,
        risposta_sbagliata_3:String
    ) {

        Log.d("domandaonfetched", domanda)
        //salviamo la domanda e le risposte
        this.domanda = chiamataApi.domanda
        this.rispostaCorretta = chiamataApi.risposta_corretta
        val rispostacorretta = chiamataApi.risposta_corretta
        val rispostasbagliata_1 = chiamataApi.risposta_sbagliata_1
        val rispostasbagliata_2 = chiamataApi.risposta_sbagliata_2
        val rispostasbagliata_3 = chiamataApi.risposta_sbagliata_3


        val listaRisposte = mutableListOf(
            rispostacorretta,
            rispostasbagliata_1,
            rispostasbagliata_2,
            rispostasbagliata_3
        )

        var i = 0
        while (i <= 3) {
            val randomIndex = Random().nextInt(listaRisposte.size)


            risposte[i] = listaRisposte[randomIndex]

            listaRisposte.removeAt(randomIndex)
            i++

            Log.d("risposta1OnFetched", risposta1)
        }

Log.d("risposta1OnFetched", risposta1)
        modClassicaActivity.chiamaConquistaSceltaMultipla(topic, domanda, risposte[0], risposte[1], risposte[2], risposte[3], rispostaCorretta)
    }

fun setDifficolta (partita:String, difficolta: String) {
    this.partita = partita
    this.difficolta = difficolta}


}

 