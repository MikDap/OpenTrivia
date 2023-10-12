package com.example.opentrivia

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.opentrivia.api.ChiamataApi
import java.util.Random


class mod_classica_conquista : Fragment(), ChiamataApi.TriviaQuestionCallback {

    private lateinit var chiamataApi: ChiamataApi
    lateinit var categoria: String
    private lateinit var difficolta: String
    private lateinit var storiaButton: Button
    private lateinit var geografiaButton: Button
    private lateinit var arteButton: Button
    private lateinit var sportButton: Button
    private lateinit var intrattenimentoButton: Button
    private lateinit var scienzeButton: Button
    var domanda : String = ""
    var rispostaCorretta : String = ""
    var risposta1: String = ""
    var risposta2: String = ""
    var risposte = arrayOf(risposta1, risposta2)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.mod_classica_conquista, container, false)

        storiaButton = view.findViewById(R.id.storiabutton)
        geografiaButton = view.findViewById(R.id.geografiabutton)
        arteButton = view.findViewById(R.id.artebutton)
        sportButton = view.findViewById(R.id.sportbutton)
        intrattenimentoButton = view.findViewById(R.id.intrattenimentobutton)
        scienzeButton = view.findViewById(R.id.scienzebutton)

        storiaButton.setOnClickListener {
            getTriviaQuestion("storia")
        }
        geografiaButton.setOnClickListener {
            getTriviaQuestion("geografia")
        }
        arteButton.setOnClickListener {
            getTriviaQuestion("arte")
        }
        sportButton.setOnClickListener {
            getTriviaQuestion("sport")
        }
        intrattenimentoButton.setOnClickListener {
            getTriviaQuestion("culturaPop")

        }
        scienzeButton.setOnClickListener {
            getTriviaQuestion("scienze")

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

        //salviamo la domanda e le risposte
        this.domanda = chiamataApi.domanda
        this.rispostaCorretta = chiamataApi.risposta_corretta
        val risposta_corretta = chiamataApi.risposta_corretta
        val risposta_sbagliata_1 = chiamataApi.risposta_sbagliata_1

    }

fun setDifficolta (difficolta: String) {
    this.difficolta = difficolta}


}

 