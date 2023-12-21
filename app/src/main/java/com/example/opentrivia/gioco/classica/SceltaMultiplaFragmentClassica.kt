package com.example.opentrivia.gioco.classica

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.opentrivia.MainActivity
import com.example.opentrivia.R
import com.example.opentrivia.utils.GiocoUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.opentrivia.utils.ModClassicaUtils

class SceltaMultiplaFragmentClassica : Fragment() {

    private lateinit var database: FirebaseDatabase
    private lateinit var domanda: TextView
    private lateinit var risposta1: Button
    private lateinit var risposta2: Button
    private lateinit var risposta3: Button
    private lateinit var risposta4: Button

    private lateinit var continua: Button


    private lateinit var modClassicaActivity: ModClassicaActivity
    private lateinit var rispostaCorretta: String

    lateinit var partita: String
    lateinit var modalita: String
    lateinit var difficolta: String
    lateinit var topic: String
    var rispostaData = false
    lateinit var uid: String
    lateinit var risposteRef: DatabaseReference
    lateinit var giocatoriRef: DatabaseReference
    lateinit var giocatoreRef: DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.mod_classica_scelta_multipla, container, false)


        modClassicaActivity = activity as ModClassicaActivity

        domanda = view.findViewById(R.id.domanda)
        risposta1 = view.findViewById(R.id.risposta1)
        risposta2 = view.findViewById(R.id.risposta2)
        risposta3 = view.findViewById(R.id.risposta3)
        risposta4 = view.findViewById(R.id.risposta4)
        continua = view.findViewById(R.id.continua)


        domanda.text = modClassicaActivity.domanda
        risposta1.text = modClassicaActivity.listaRisposte[0]
        risposta2.text = modClassicaActivity.listaRisposte[1]
        risposta3.text = modClassicaActivity.listaRisposte[2]
        risposta4.text = modClassicaActivity.listaRisposte[3]
        rispostaCorretta = modClassicaActivity.rispostaCorretta

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        database = FirebaseDatabase.getInstance()

         uid = FirebaseAuth.getInstance().currentUser?.uid.toString()

         risposteRef =
            database.getReference("partite").child(modalita).child(difficolta).child(partita)
                .child("giocatori").child(uid).child(topic)

         giocatoreRef =
            database.getReference("partite").child(modalita).child(difficolta).child(partita)
                .child("giocatori").child(uid)

         giocatoriRef = database.getReference("partite").child(modalita).child(difficolta).child(partita).child("giocatori")



        risposta1.setOnClickListener {
          controllaRisposta(risposta1)
        }

        risposta2.setOnClickListener {
           controllaRisposta(risposta2)
        }

        risposta3.setOnClickListener {
         controllaRisposta(risposta3)
        }

        risposta4.setOnClickListener {
          controllaRisposta(risposta4)
        }


    }






    fun updateContinuaButton(
        giocatoreRef: DatabaseReference,tipo: String
    ) {
        giocatoreRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(giocatore: DataSnapshot) {

                val giocatoriRef = database.getReference("partite").child(modalita).child(difficolta).child(partita).child("giocatori")
                val partitaRef = database.getReference("partite").child(modalita).child(difficolta).child(partita)
                //chiamo conquista
                if (giocatore.hasChild("risposteTotCorrette")) {

                    var risposte_corrette =
                        giocatore.child("risposteTotCorrette").value.toString().toInt()


                    if (risposte_corrette == 3) {

                        giocatoreRef.child("risposteTotCorrette").setValue(0)


                        Handler(Looper.getMainLooper()).postDelayed({
                            continua.visibility = View.VISIBLE
                        }, 1500)

                        continua.setOnClickListener {
                            modClassicaActivity.chiamaConquista()
                        }
                    }

                    else {

                        if (tipo == "corretta") {
                            Handler(Looper.getMainLooper()).postDelayed({
                                continua.visibility = View.VISIBLE
                            }, 1500)

                            continua.setOnClickListener {
                                modClassicaActivity.chiamaRuota()
                            }

                        } else if (tipo == "sbagliata") {

                            ModClassicaUtils.ottieniNomeAvversario(giocatoriRef){
                                    nomeAvversario ->

                                if (nomeAvversario == "non hai un avversario"){

                                    partitaRef.child("Turno").setValue("-")
                                }
                                else {
                                    partitaRef.child("Turno").setValue(nomeAvversario)
                                }

                            }
                            Handler(Looper.getMainLooper()).postDelayed({
                                continua.visibility = View.VISIBLE
                            }, 1500)

                            continua.setOnClickListener {
                                // Torna al menu
                                val intent = Intent(requireContext(), MainActivity::class.java)
                                startActivity(intent)
                                requireActivity().finish()

                            }

                        }

                    }


                }

                else {
                    if (tipo == "corretta") {
                        Handler(Looper.getMainLooper()).postDelayed({
                            continua.visibility = View.VISIBLE
                        }, 1500)

                        continua.setOnClickListener {
                            modClassicaActivity.chiamaRuota()
                        }

                    } else if (tipo == "sbagliata") {

                        ModClassicaUtils.ottieniNomeAvversario(giocatoriRef){
                            nomeAvversario ->

                            if (nomeAvversario == "non hai un avversario"){

                                partitaRef.child("Turno").setValue("-")
                            }
                            else {
                                partitaRef.child("Turno").setValue(nomeAvversario)
                            }

                        }

                        Handler(Looper.getMainLooper()).postDelayed({
                            continua.visibility = View.VISIBLE
                        }, 1500)
                        continua.setOnClickListener {
                            // Torna al menu
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            startActivity(intent)
                            requireActivity().finish()
                        }
                    }

                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


    fun controllaRisposta(risposta: Button){

        if (!rispostaData) {


            if (GiocoUtils.QuestaèLaRispostaCorretta(risposta, rispostaCorretta)) {
                ModClassicaUtils.updateRisposte(risposteRef, "corretta")

                ModClassicaUtils.updateContatoreRisposteCorrette(giocatoriRef) { ->
                    //aggiorna ScrollView
                    ModClassicaUtils.ottieniNomeAvversario_e_argomentiConquistati(giocatoriRef) { nomeAvversario,idAvversario, argomenti_conquistati_miei, argomenti_conquistati_avversario ->
                        ModClassicaUtils.updateScrollView(
                            nomeAvversario,
                            idAvversario,
                            argomenti_conquistati_miei,
                            argomenti_conquistati_avversario,
                            partita,
                            difficolta,
                            database
                        )
                    }

                    updateContinuaButton(giocatoreRef, "corretta")
                    GiocoUtils.updateStatTopic(topic,"corretta")
                }
            }

            else {
                ModClassicaUtils.updateRisposte(risposteRef, "sbagliata")
                ModClassicaUtils.ottieniNomeAvversario_e_argomentiConquistati(giocatoriRef){
                        nomeAvversario,idAvversario, argomenti_conquistati_miei, argomenti_conquistati_avversario ->
                    ModClassicaUtils.updateScrollView(nomeAvversario,idAvversario, argomenti_conquistati_miei, argomenti_conquistati_avversario,partita, difficolta, database)
                }

                updateContinuaButton(giocatoreRef,"sbagliata")
                GiocoUtils.updateStatTopic(topic,"sbagliata")
            }

            rispostaData = true

        }

    }
}

