package com.example.opentrivia.gioco.classica

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
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

    private var database = FirebaseDatabase.getInstance()
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
    var uid = FirebaseAuth.getInstance().currentUser?.uid.toString()

    lateinit var giocatoriRef: DatabaseReference
    lateinit var giocatoreRef: DatabaseReference
    lateinit var risposteRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.mod_classica_scelta_multipla, container, false)

        giocatoriRef = database.getReference("partite").child(modalita).child(difficolta).child(partita).child("giocatori")
        giocatoreRef = giocatoriRef.child(uid)
        risposteRef = giocatoreRef.child(topic)

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

        onBackPressed()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



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

                            GiocoUtils.getAvversario(modalita, difficolta, partita){ giocatore2esiste, avversario, nomeAvv ->

                                if (nomeAvv == "-"){

                                    partitaRef.child("Turno").setValue("-")
                                }
                                else {
                                    partitaRef.child("Turno").setValue(nomeAvv)
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

                        GiocoUtils.getAvversario(modalita, difficolta, partita){ giocatore2esiste, avversario, nomeAvv ->

                            if (nomeAvv == "-"){

                                partitaRef.child("Turno").setValue("-")
                            }
                            else {
                                partitaRef.child("Turno").setValue(nomeAvv)
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
                GiocoUtils.updateRisposte(risposteRef, "risposteCorrette")

                ModClassicaUtils.updateContatoreRisposteCorrette(giocatoriRef) {

                    GiocoUtils.getAvversario(modalita, difficolta, partita){ giocatore2esiste, avversario, nomeAvv ->
                        ModClassicaUtils.getArgomentiConquistati(giocatoriRef){ argomentiMiei, argomentiAvversario ->

                            val argomenti_conquistati_miei = argomentiMiei.size
                            val argomenti_conquistati_avversario = argomentiAvversario.size

                            ModClassicaUtils.updateScrollView(nomeAvv,avversario, argomenti_conquistati_miei, argomenti_conquistati_avversario,partita, difficolta, database)
                        }
                    }

                    updateContinuaButton(giocatoreRef, "corretta")
                    GiocoUtils.updateStatTopic(topic,"corretta")
                }
            }

            else {
                GiocoUtils.updateRisposte(risposteRef, "risposteSbagliate")

                GiocoUtils.getAvversario(modalita, difficolta, partita){ giocatore2esiste, avversario, nomeAvv ->

                    ModClassicaUtils.getArgomentiConquistati(giocatoriRef){ argomentiMiei, argomentiAvversario ->

                        val argomenti_conquistati_miei = argomentiMiei.size
                        val argomenti_conquistati_avversario = argomentiAvversario.size

                        ModClassicaUtils.updateScrollView(nomeAvv,avversario, argomenti_conquistati_miei, argomenti_conquistati_avversario,partita, difficolta, database)
                    }
                }

                updateContinuaButton(giocatoreRef,"sbagliata")
                GiocoUtils.updateStatTopic(topic,"sbagliata")
            }

            rispostaData = true

        }

    }

    fun onBackPressed(){
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Vuoi ritornare al menù?")
                alertDialog.setMessage("ATTENZIONE: uscendo la risposta sarà considerata sbagliata")

                alertDialog.setPositiveButton("SI") { dialog: DialogInterface, which: Int ->

                    updateContinuaButton(giocatoreRef,"sbagliata")
                    val intent = Intent(requireContext(), MainActivity::class.java)
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

