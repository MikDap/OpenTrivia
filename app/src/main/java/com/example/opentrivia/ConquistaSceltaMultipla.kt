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
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.ConstraintSet.Layout
import com.example.opentrivia.gioco.ModClassicaActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.opentrivia.utils.ModClassicaUtils

class ConquistaSceltaMultipla : Fragment() {

    private lateinit var database: FirebaseDatabase
    private lateinit var partita: String
    private lateinit var difficolta: String
    private lateinit var NomeArgomento: TextView
    private lateinit var domanda: TextView
    private lateinit var risposta1: Button
    private lateinit var risposta2: Button
    private lateinit var risposta3: Button
    private lateinit var risposta4: Button
    private lateinit var rispostaCorretta: String
    private lateinit var modClassicaActivity: ModClassicaActivity
    private lateinit var layout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.mod_conquista_sceltamultipla, container, false)

        layout = view.findViewById(R.id.layout2)
        domanda = view.findViewById(R.id.domanda)
        risposta1 = view.findViewById(R.id.risposta1)
        risposta2 = view.findViewById(R.id.risposta2)
        risposta3 = view.findViewById(R.id.risposta3)
        risposta4 = view.findViewById(R.id.risposta4)
        NomeArgomento = view.findViewById(R.id.materiaTextView)

        modClassicaActivity = activity as ModClassicaActivity


        NomeArgomento.text = modClassicaActivity.topicConquista
        domanda.text = modClassicaActivity.domandaConquista
        risposta1.text = modClassicaActivity.risposta1Conquista
        risposta2.text = modClassicaActivity.risposta2Conquista
        risposta3.text = modClassicaActivity.risposta3Conquista
        risposta4.text = modClassicaActivity.risposta4Conquista
        rispostaCorretta = modClassicaActivity.rispostaCorrettaConquista
        partita = modClassicaActivity.partita
        difficolta = modClassicaActivity.difficolta

coloraSfondo(NomeArgomento.text.toString())

    return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val giocatoreRef =
            database.getReference("partite").child("classica").child(difficolta).child(partita).child("giocatori").child(uid)
        val giocatoriRef = database.getReference("partite").child("classica").child(difficolta).child(partita)
            .child("giocatori")




        var rispostaData = false
        risposta1.setOnClickListener {
            if (!rispostaData) {

                if (risposta1.text == rispostaCorretta) {
                    risposta1.setBackgroundColor(Color.LTGRAY)
                    Handler(Looper.getMainLooper()).postDelayed({
                        risposta1.setBackgroundColor(Color.GREEN)
                    }, 500)

                    updateArgomentiConquistati(giocatoreRef){
                        //callback di updateArgomentiConquistati
                        ModClassicaUtils.ottieniNomeAvversario_e_argomentiConquistati(giocatoriRef) {

                                nomeAvversario,idAvversario, argomenti_conquistati_miei, argomenti_conquistati_avversario ->
                            // CHIAMA VITTORIA SE ARGOMENTICONQUISTATI MIEI = 6
                            if (argomenti_conquistati_miei == 6) {
                                modClassicaActivity.schermataVittoria(nomeAvversario,argomenti_conquistati_miei,argomenti_conquistati_avversario)
                                StatisticheFragment.StatisticheTerminate(partita,"classica",difficolta,uid,argomenti_conquistati_miei,argomenti_conquistati_avversario)
                                StatisticheFragment.StatisticheTerminate(partita,"classica",difficolta,idAvversario,argomenti_conquistati_miei,argomenti_conquistati_avversario)
                            }
                            ModClassicaUtils.updateScrollView(nomeAvversario,idAvversario,argomenti_conquistati_miei, argomenti_conquistati_avversario, partita, difficolta, database)

                        }
                    }
                    StatisticheFragment.updateStatTopic(modClassicaActivity.topicConquista,"corretta")
                    Handler(Looper.getMainLooper()).postDelayed({
                        modClassicaActivity.chiamaRuota()
                    }, 1000)

                } else {
                    risposta1.setBackgroundColor(Color.LTGRAY)
                    Handler(Looper.getMainLooper()).postDelayed({
                        risposta1.setBackgroundColor(Color.RED)
                    }, 500)

                    ModClassicaUtils.ottieniNomeAvversario_e_argomentiConquistati(giocatoriRef) {

                            nomeAvversario,idAvversario, argomenti_conquistati_miei, argomenti_conquistati_avversario ->
                        ModClassicaUtils.updateScrollView(nomeAvversario,idAvversario,argomenti_conquistati_miei, argomenti_conquistati_avversario, partita, difficolta, database)

                    }


                    ModClassicaUtils.ottieniNomeAvversario(giocatoriRef) { nomeAvversario ->

                        val partitaRef =
                            database.getReference("partite").child("classica").child(difficolta)
                                .child(partita)
                        if (nomeAvversario == "non hai un avversario") {

                            partitaRef.child("Turno").setValue("-")
                        } else {
                            partitaRef.child("Turno").setValue(nomeAvversario)
                        }
                    }
                    StatisticheFragment.updateStatTopic(modClassicaActivity.topicConquista,"sbagliata")
                    Handler(Looper.getMainLooper()).postDelayed({
                        modClassicaActivity.chiamaRuota()
                    }, 1000)
                }
                rispostaData = true

            }
        }

        risposta2.setOnClickListener {
            if (!rispostaData) {
                if (risposta2.text == rispostaCorretta) {
                    risposta2.setBackgroundColor(Color.LTGRAY)
                    Handler(Looper.getMainLooper()).postDelayed({
                        risposta2.setBackgroundColor(Color.GREEN)
                    }, 500)

                    updateArgomentiConquistati(giocatoreRef){
                        //callback di updateArgomentiConquistati
                        ModClassicaUtils.ottieniNomeAvversario_e_argomentiConquistati(giocatoriRef) {

                                nomeAvversario,idAvversario, argomenti_conquistati_miei, argomenti_conquistati_avversario ->
                            // CHIAMA VITTORIA SE ARGOMENTICONQUISTATI MIEI = 6
                            if (argomenti_conquistati_miei == 6) {
                                modClassicaActivity.schermataVittoria(nomeAvversario,argomenti_conquistati_miei,argomenti_conquistati_avversario)
                                StatisticheFragment.StatisticheTerminate(partita,"classica",difficolta,uid,argomenti_conquistati_miei,argomenti_conquistati_avversario)
                                StatisticheFragment.StatisticheTerminate(partita,"classica",difficolta,idAvversario,argomenti_conquistati_miei,argomenti_conquistati_avversario)
                            }
                            ModClassicaUtils.updateScrollView(nomeAvversario,idAvversario,argomenti_conquistati_miei, argomenti_conquistati_avversario, partita, difficolta, database)

                        }
                    }
                    StatisticheFragment.updateStatTopic(modClassicaActivity.topicConquista,"corretta")
                    Handler(Looper.getMainLooper()).postDelayed({
                        modClassicaActivity.chiamaRuota()
                    }, 1000)
                } else {
                    risposta2.setBackgroundColor(Color.LTGRAY)
                    Handler(Looper.getMainLooper()).postDelayed({
                        risposta2.setBackgroundColor(Color.RED)
                    }, 500)

                    ModClassicaUtils.ottieniNomeAvversario_e_argomentiConquistati(giocatoriRef) {

                            nomeAvversario,idAvversario, argomenti_conquistati_miei, argomenti_conquistati_avversario ->
                        ModClassicaUtils.updateScrollView(nomeAvversario,idAvversario,argomenti_conquistati_miei, argomenti_conquistati_avversario, partita, difficolta, database)

                    }

                    ModClassicaUtils.ottieniNomeAvversario(giocatoriRef) { nomeAvversario ->

                        val partitaRef =
                            database.getReference("partite").child("classica").child(difficolta)
                                .child(partita)
                        if (nomeAvversario == "non hai un avversario") {

                            partitaRef.child("Turno").setValue("-")
                        } else {
                            partitaRef.child("Turno").setValue(nomeAvversario)
                        }
                    }

                    StatisticheFragment.updateStatTopic(modClassicaActivity.topicConquista,"sbagliata")
                    Handler(Looper.getMainLooper()).postDelayed({
                        modClassicaActivity.chiamaRuota()
                    }, 1000)

                }
                rispostaData = true
            }
        }


        risposta3.setOnClickListener {
            if (!rispostaData) {
                if (risposta3.text == rispostaCorretta) {
                    risposta3.setBackgroundColor(Color.LTGRAY)
                    Handler(Looper.getMainLooper()).postDelayed({
                        risposta3.setBackgroundColor(Color.GREEN)
                    }, 500)

                    updateArgomentiConquistati(giocatoreRef){
                        //callback di updateArgomentiConquistati
                        ModClassicaUtils.ottieniNomeAvversario_e_argomentiConquistati(giocatoriRef) {
                                nomeAvversario,idAvversario, argomenti_conquistati_miei, argomenti_conquistati_avversario ->
                            // CHIAMA VITTORIA SE ARGOMENTICONQUISTATI MIEI = 6
                            if (argomenti_conquistati_miei == 6) {
                                modClassicaActivity.schermataVittoria(nomeAvversario,argomenti_conquistati_miei,argomenti_conquistati_avversario)
                                StatisticheFragment.StatisticheTerminate(partita,"classica",difficolta,uid,argomenti_conquistati_miei,argomenti_conquistati_avversario)
                                StatisticheFragment.StatisticheTerminate(partita,"classica",difficolta,idAvversario,argomenti_conquistati_miei,argomenti_conquistati_avversario)
                            }
                            ModClassicaUtils.updateScrollView(nomeAvversario,idAvversario,argomenti_conquistati_miei, argomenti_conquistati_avversario, partita, difficolta, database)

                        }
                    }
                    StatisticheFragment.updateStatTopic(modClassicaActivity.topicConquista,"corretta")
                    Handler(Looper.getMainLooper()).postDelayed({
                        modClassicaActivity.chiamaRuota()
                    }, 1000)
                } else {
                    risposta3.setBackgroundColor(Color.LTGRAY)
                    Handler(Looper.getMainLooper()).postDelayed({
                        risposta3.setBackgroundColor(Color.RED)
                    }, 500)

                    ModClassicaUtils.ottieniNomeAvversario_e_argomentiConquistati(giocatoriRef) {

                            nomeAvversario,idAvversario, argomenti_conquistati_miei, argomenti_conquistati_avversario ->
                        ModClassicaUtils.updateScrollView(nomeAvversario,idAvversario,argomenti_conquistati_miei, argomenti_conquistati_avversario, partita, difficolta, database)

                    }

                    ModClassicaUtils.ottieniNomeAvversario(giocatoriRef) { nomeAvversario ->

                        val partitaRef =
                            database.getReference("partite").child("classica").child(difficolta)
                                .child(partita)
                        if (nomeAvversario == "non hai un avversario") {

                            partitaRef.child("Turno").setValue("-")
                        } else {
                            partitaRef.child("Turno").setValue(nomeAvversario)
                        }
                    }

                    StatisticheFragment.updateStatTopic(modClassicaActivity.topicConquista,"sbagliata")
                    Handler(Looper.getMainLooper()).postDelayed({
                        modClassicaActivity.chiamaRuota()
                    }, 1000)

                }
                rispostaData = true
            }
        }


        risposta4.setOnClickListener {
            if (!rispostaData) {
                if (risposta4.text == rispostaCorretta) {
                    risposta4.setBackgroundColor(Color.LTGRAY)
                    Handler(Looper.getMainLooper()).postDelayed({
                        risposta4.setBackgroundColor(Color.GREEN)
                    }, 500)

                    updateArgomentiConquistati(giocatoreRef){
                        //callback di updateArgomentiConquistati
                        ModClassicaUtils.ottieniNomeAvversario_e_argomentiConquistati(giocatoriRef) {

                                nomeAvversario,idAvversario, argomenti_conquistati_miei, argomenti_conquistati_avversario ->
                            // CHIAMA VITTORIA SE ARGOMENTICONQUISTATI MIEI = 6
                            if (argomenti_conquistati_miei == 6) {
                                modClassicaActivity.schermataVittoria(nomeAvversario,argomenti_conquistati_miei,argomenti_conquistati_avversario)
                                StatisticheFragment.StatisticheTerminate(partita,"classica",difficolta,uid,argomenti_conquistati_miei,argomenti_conquistati_avversario)
                                StatisticheFragment.StatisticheTerminate(partita,"classica",difficolta,idAvversario,argomenti_conquistati_miei,argomenti_conquistati_avversario)
                            }
                            ModClassicaUtils.updateScrollView(nomeAvversario,idAvversario,argomenti_conquistati_miei, argomenti_conquistati_avversario, partita, difficolta, database)

                        }
                    }

                    StatisticheFragment.updateStatTopic(modClassicaActivity.topicConquista,"corretta")
                    Handler(Looper.getMainLooper()).postDelayed({
                        modClassicaActivity.chiamaRuota()
                    }, 1000)
                } else {
                    risposta4.setBackgroundColor(Color.LTGRAY)
                    Handler(Looper.getMainLooper()).postDelayed({
                        risposta4.setBackgroundColor(Color.RED)
                    }, 500)


                    ModClassicaUtils.ottieniNomeAvversario_e_argomentiConquistati(giocatoriRef) {

                            nomeAvversario,idAvversario, argomenti_conquistati_miei, argomenti_conquistati_avversario ->
                        ModClassicaUtils.updateScrollView(nomeAvversario,idAvversario,argomenti_conquistati_miei, argomenti_conquistati_avversario, partita, difficolta, database)

                    }

                    ModClassicaUtils.ottieniNomeAvversario(giocatoriRef) { nomeAvversario ->

                        val partitaRef =
                            database.getReference("partite").child("classica").child(difficolta)
                                .child(partita)
                        if (nomeAvversario == "non hai un avversario") {

                            partitaRef.child("Turno").setValue("-")
                        } else {
                            partitaRef.child("Turno").setValue(nomeAvversario)
                        }
                    }

                    StatisticheFragment.updateStatTopic(modClassicaActivity.topicConquista,"sbagliata")
                    Handler(Looper.getMainLooper()).postDelayed({
                        modClassicaActivity.chiamaRuota()
                    }, 1000)
                }
                rispostaData = true
            }
        }


    }














    fun updateArgomentiConquistati(giocatoreRef: DatabaseReference,
     callback: () -> Unit
    ) {

        giocatoreRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(giocatore: DataSnapshot) {
                    giocatoreRef.child("ArgomentiConquistati").child(NomeArgomento.text.toString()).setValue("si")
                callback()
            }





            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }




fun coloraSfondo (topic: String) {

    when (topic) {
        "storia" -> layout.setBackgroundColor(Color.parseColor("#FFBB2F"))
        "geografia" -> layout.setBackgroundColor(Color.parseColor("#0000FF"))
        "arte" -> layout.setBackgroundColor(Color.RED)  // il rosso da problemi
        "sport" -> layout.setBackgroundColor(Color.parseColor("#FFEB3B"))
        "culturaPop" -> layout.setBackgroundColor(Color.parseColor("#FF00FF"))
        "scienze" -> layout.setBackgroundColor(Color.parseColor("#4CAF50"))


    }


}

}