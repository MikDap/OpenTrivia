package com.example.opentrivia.gioco.classica

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.opentrivia.menu.MenuActivity
import com.example.opentrivia.R
import com.example.opentrivia.utils.DatabaseUtils
import com.example.opentrivia.utils.GiocoUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.opentrivia.utils.ModClassicaUtils

class ConquistaSceltaMultiplaFragment : Fragment() {

    var database = FirebaseDatabase.getInstance()
    var uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private lateinit var giocatoreRef: DatabaseReference
    private lateinit var giocatoriRef: DatabaseReference
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

        giocatoreRef = database.getReference("partite").child("classica").child(difficolta).child(partita).child("giocatori").child(uid)
        giocatoriRef = database.getReference("partite").child("classica").child(difficolta).child(partita).child("giocatori")

coloraSfondo(NomeArgomento.text.toString())
onBackPressed()
    return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        val listaRisposte = listOf(risposta1, risposta2, risposta3, risposta4)
        var rispostaData = false


        risposta1.setOnClickListener {
            if (!rispostaData) {
                controllaRisposta(risposta1)
                GiocoUtils.evidenziaRispostaCorretta(listaRisposte, rispostaCorretta)
                rispostaData = true
            }
        }

        risposta2.setOnClickListener {
            if (!rispostaData) {
                controllaRisposta(risposta2)
                GiocoUtils.evidenziaRispostaCorretta(listaRisposte, rispostaCorretta)
                rispostaData = true
            }
        }

        risposta3.setOnClickListener {
            if (!rispostaData) {
                controllaRisposta(risposta3)
                GiocoUtils.evidenziaRispostaCorretta(listaRisposte, rispostaCorretta)
                rispostaData = true
            }
        }
        risposta4.setOnClickListener {
            if (!rispostaData) {
                controllaRisposta(risposta4)
                GiocoUtils.evidenziaRispostaCorretta(listaRisposte, rispostaCorretta)
                rispostaData = true
            }
        }


    }




    fun controllaRisposta(risposta: Button){

        val giocatoriRef = database.getReference("partite").child("classica").child(difficolta).child(partita)
            .child("giocatori")
        if (risposta.text == rispostaCorretta) {
            risposta.setBackgroundColor(Color.LTGRAY)
            Handler(Looper.getMainLooper()).postDelayed({
                risposta.setBackgroundColor(Color.GREEN)
            }, 500)

            updateArgomentiConquistati(giocatoreRef){
                //callback di updateArgomentiConquistati
                DatabaseUtils.getAvversario("classica", difficolta, partita){ giocatore2esiste, avversario, nomeAvv ->

                    ModClassicaUtils.getArgomentiConquistati(giocatoriRef){ argomentiMiei, argomentiAvversario ->

                        val argomenti_conquistati_miei = argomentiMiei.size
                        val argomenti_conquistati_avversario = argomentiAvversario.size

                    // CHIAMA VITTORIA SE ARGOMENTICONQUISTATI MIEI = 6
                    if (argomenti_conquistati_miei == 6) {
                        GiocoUtils.schermataVittoria(requireActivity().supportFragmentManager, R.id.fragmentContainerViewGioco, nomeAvv, argomenti_conquistati_miei, argomenti_conquistati_avversario, "classica")
                        DatabaseUtils.spostaInPartiteTerminate(partita,"classica",difficolta,uid,argomenti_conquistati_miei,argomenti_conquistati_avversario)
                        DatabaseUtils.spostaInPartiteTerminate(partita,"classica",difficolta,avversario,argomenti_conquistati_miei,argomenti_conquistati_avversario)
                        DatabaseUtils.updateStatTopic(modClassicaActivity.topicConquista,"corretta")
                    }
                    else {
                        ModClassicaUtils.updateScrollView(nomeAvv,avversario,argomenti_conquistati_miei, argomenti_conquistati_avversario, partita, difficolta, database)
                        DatabaseUtils.updateStatTopic(modClassicaActivity.topicConquista,"corretta")
                        Handler(Looper.getMainLooper()).postDelayed({
                            modClassicaActivity.chiamaRuota()
                        }, 1000)
                    }

                  }
                }
            }
            DatabaseUtils.updateStatTopic(modClassicaActivity.topicConquista,"corretta")
            Handler(Looper.getMainLooper()).postDelayed({
                modClassicaActivity.chiamaRuota()
            }, 1000)
        } else {
            risposta.setBackgroundColor(Color.LTGRAY)
            Handler(Looper.getMainLooper()).postDelayed({
                risposta.setBackgroundColor(Color.RED)
            }, 500)


            DatabaseUtils.getAvversario("classica", difficolta, partita){ giocatore2esiste, avversario, nomeAvv ->

                val partitaRef =
                    database.getReference("partite").child("classica").child(difficolta)
                        .child(partita)
                if (nomeAvv == "-") {

                    partitaRef.child("Turno").setValue("-")
                } else {
                    partitaRef.child("Turno").setValue(nomeAvv)
                }
            }

            DatabaseUtils.updateStatTopic(modClassicaActivity.topicConquista,"sbagliata")
            val intent = Intent(activity, MenuActivity::class.java)
            startActivity(intent)

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



    fun onBackPressed(){
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Vuoi ritornare al menù?")
                alertDialog.setMessage("ATTENZIONE: uscendo la risposta sarà considerata sbagliata")

                alertDialog.setPositiveButton("SI") { dialog: DialogInterface, which: Int ->

                    DatabaseUtils.getAvversario("classica", difficolta, partita){ giocatore2esiste, avversario, nomeAvv ->

                        val partitaRef = database.getReference("partite").child("classica").child(difficolta).child(partita)
                        if (nomeAvv == "-") {
                            partitaRef.child("Turno").setValue("-")
                        } else {
                            partitaRef.child("Turno").setValue(nomeAvv)
                        }

                    }

                    DatabaseUtils.updateStatTopic(modClassicaActivity.topicConquista,"sbagliata")
                    val intent = Intent(activity, MenuActivity::class.java)
                    startActivity(intent)

                    requireActivity().finish()
                }

                alertDialog.setNegativeButton("NO") { dialog: DialogInterface, which: Int ->
                    dialog.dismiss()
                }

                alertDialog.show()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,callback)
    }
}