package com.example.opentrivia

import android.graphics.Color
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
import androidx.cardview.widget.CardView
import com.example.opentrivia.api.ChiamataApi
import com.example.opentrivia.gioco.ModClassicaActivity
import com.example.opentrivia.gioco.SceltaMultiplaFragmentClassica
import com.example.opentrivia.utils.ModClassicaUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.Random


class mod_classica_conquista : Fragment(), ChiamataApi.TriviaQuestionCallback {

    private lateinit var chiamataApi: ChiamataApi
    lateinit var categoria: String
    private lateinit var partita: String
    private lateinit var difficolta: String
    private lateinit var storiaButton: CardView
    private lateinit var geografiaButton: CardView
    private lateinit var arteButton: CardView
    private lateinit var sportButton: CardView
    private lateinit var intrattenimentoButton: CardView
    private lateinit var scienzeButton: CardView
    private lateinit var storia: View
    private lateinit var sport: View
    private lateinit var geografia: View
    private lateinit var arte: View
    private lateinit var scienze: View
    private lateinit var culturaPop: View
    private lateinit var storia2: View
    private lateinit var sport2: View
    private lateinit var geografia2: View
    private lateinit var arte2: View
    private lateinit var scienze2: View
    private lateinit var culturaPop2: View
    var domanda: String = ""
    var rispostaCorretta: String = ""
    var risposta1: String = ""
    var risposta2: String = ""
    var risposta3: String = ""
    var risposta4: String = ""
    var risposte = arrayOf(risposta1, risposta2, risposta3, risposta4)
    private lateinit var database: FirebaseDatabase
    private lateinit var modClassicaActivity: ModClassicaActivity
    lateinit var topic: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.mod_classica_conquista, container, false)

        //quadratini
        storia = view.findViewById(R.id.storia)
        sport = view.findViewById(R.id.sport)
        geografia = view.findViewById(R.id.geografia)
        arte = view.findViewById(R.id.arte)
        scienze = view.findViewById(R.id.scienze)
        culturaPop = view.findViewById(R.id.culturaPop)

        storia2 = view.findViewById(R.id.storia2)
        sport2 = view.findViewById(R.id.sport2)
        geografia2 = view.findViewById(R.id.geografia2)
        arte2 = view.findViewById(R.id.arte2)
        scienze2 = view.findViewById(R.id.scienze2)
        culturaPop2 = view.findViewById(R.id.culturaPop2)

        modClassicaActivity = activity as ModClassicaActivity
        val partita = modClassicaActivity.partita
        val modalita = "classica"
        val difficolta = modClassicaActivity.difficolta

        database = FirebaseDatabase.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val giocatoreRef =
            database.getReference("partite").child(modalita).child(difficolta).child(partita)
                .child("giocatori").child(uid)
        val giocatoriRef =
            database.getReference("partite").child(modalita).child(difficolta).child(partita)
                .child("giocatori")

        ModClassicaUtils.QualiArgomentiConquistati(giocatoriRef) { argomentiMiei, argomentiAvversario ->
            if (argomentiMiei.isEmpty()) Log.d("argomentiMieiVuoti", "si")
            if (argomentiMiei.isNotEmpty()) {
                Log.d("argomentiMiei", argomentiMiei[0])
            }
            coloraQuadratini(argomentiMiei, true)
            coloraQuadratini(argomentiAvversario, false)
        }

        //button
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
        val categorie_culturaPop =
            arrayOf("9", "10", "11", "12", "13", "14", "15", "16", "29", "31", "32")
        val categorie_scienze = arrayOf("17", "18", "19", "30")

        when (topic) {

            "culturaPop" -> {
                categoria = getRandomCategoria(categorie_culturaPop)
            }

            "sport" -> {
                categoria = "21"
            }

            "storia" -> {
                categoria = "23"
            }

            "geografia" -> {
                categoria = "22"
            }

            "arte" -> {
                categoria = "25"
            }

            "scienze" -> {
                categoria = getRandomCategoria(categorie_scienze)
            }

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
        chiamataApi = ChiamataApi("multiple", categoria, difficolta)
        chiamataApi.fetchTriviaQuestion(this)
        Log.d("getTriviaQuestion", "siii")

    }

    override fun onTriviaQuestionFetched(
        tipo: String,
        domanda: String,
        risposta_corretta: String,
        risposta_sbagliata_1: String,
        risposta_sbagliata_2: String,
        risposta_sbagliata_3: String
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
        modClassicaActivity.chiamaConquistaSceltaMultipla(
            topic,
            domanda,
            risposte[0],
            risposte[1],
            risposte[2],
            risposte[3],
            rispostaCorretta
        )
    }

    fun setDifficolta(partita: String, difficolta: String) {
        this.partita = partita
        this.difficolta = difficolta
    }


    fun coloraQuadratini(Argomenti: ArrayList<String>, miei: Boolean) {

        for (argomento in Argomenti) {

            if (miei) {
                when (argomento) {

                    "storia" -> {
                        storia.setBackgroundColor(Color.parseColor("#FFBB2F"))
                    }

                    "sport" -> {
                        sport.setBackgroundColor(Color.parseColor("#FFEB3B"))
                    }

                    "geografia" -> {
                        geografia.setBackgroundColor(Color.parseColor("#0000FF"))
                    }

                    "arte" -> {
                        arte.setBackgroundColor(Color.parseColor("#FF0006"))
                    }

                    "scienze" -> {
                        scienze.setBackgroundColor(Color.parseColor("#4CAF50"))
                    }

                    "culturaPop" -> {
                        culturaPop.setBackgroundColor(Color.parseColor("#FF00FF"))
                    }


                }

            } else {
                when (argomento) {

                    "storia" -> {
                        storia2.setBackgroundColor(Color.parseColor("#FFBB2F"))
                    }

                    "sport" -> {
                        sport2.setBackgroundColor(Color.parseColor("#FFEB3B"))
                    }

                    "geografia" -> {
                        geografia2.setBackgroundColor(Color.parseColor("#0000FF"))
                    }

                    "arte" -> {
                        arte2.setBackgroundColor(Color.parseColor("#FF0006"))
                    }

                    "scienze" -> {
                        scienze2.setBackgroundColor(Color.parseColor("#4CAF50"))
                    }

                    "culturaPop" -> {
                        culturaPop2.setBackgroundColor(Color.parseColor("#FF00FF"))
                    }


                }

            }
        }


    }
}