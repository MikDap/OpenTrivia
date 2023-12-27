package com.example.opentrivia.gioco.argomento_singolo

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
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


class SceltaMultiplaFragmentArgSingolo : Fragment() {

    private lateinit var database: FirebaseDatabase
    private lateinit var domanda: TextView
    private lateinit var risposta1: Button
    private lateinit var risposta2: Button
    private lateinit var risposta3: Button
    private lateinit var risposta4: Button
    private lateinit var modArgomentoActivity: ModArgomentoActivity


    private lateinit var rispostaCorretta: String


    private lateinit var partita: String
    private lateinit var modalita: String
    private lateinit var difficolta: String
    private lateinit var topic: String

    var rispostaData = false
    private lateinit var uid:String
    private lateinit var risposteRef: DatabaseReference
    private var ritirato = false
    private var avvRitirato = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =
            inflater.inflate(R.layout.mod_argomento_singolo_scelta_multipla, container, false)

        database = FirebaseDatabase.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()

        var ritiratoRef =
            database.getReference("partite").child(modalita).child(difficolta).child(partita)
                .child("giocatori").child(uid)

        modArgomentoActivity = activity as ModArgomentoActivity

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Vuoi ritornare al menù?")
                alertDialog.setMessage("ATTENZIONE: uscendo perderai la partita")

                alertDialog.setPositiveButton("SI") { dialog: DialogInterface, which: Int ->

                    ritiratoRef.child("ritirato").setValue("si")
                    ritirato = true
                    finePartita()
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

        domanda = view.findViewById(R.id.domanda)
        risposta1 = view.findViewById(R.id.risposta1)
        risposta2 = view.findViewById(R.id.risposta2)
        risposta3 = view.findViewById(R.id.risposta3)
        risposta4 = view.findViewById(R.id.risposta4)

        Log.d(modalita, "modalita")


        domanda.text = modArgomentoActivity.domanda
        risposta1.text = modArgomentoActivity.listaRisposte[0]
        Log.d("risposta1", risposta1.text as String)
        risposta2.text = modArgomentoActivity.listaRisposte[1]
        risposta3.text = modArgomentoActivity.listaRisposte[2]
        risposta4.text = modArgomentoActivity.listaRisposte[3]
        rispostaCorretta = modArgomentoActivity.rispostaCorretta

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        database = FirebaseDatabase.getInstance()
         uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
         risposteRef = database.getReference("partite").child(modalita).child(difficolta).child(partita)
                .child("giocatori").child(uid).child(topic)

             controllaRitiro()

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

    fun setParametriPartita(
        partita: String,
        modalita: String,
        difficolta: String,
        topic: String
    ) {
        this.partita = partita
        this.modalita = modalita
        this.difficolta = difficolta
        this.topic = topic
    }


    fun finePartita() {
        database = FirebaseDatabase.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        var giocatoriRef =
            database.getReference("partite").child(modalita).child(difficolta).child(partita)
                .child("giocatori")
        var risposte1 = 0
        var risposte2 = 0
        var giocatore2esiste = false
        var avversario = ""
        var nomeAvv = ""
        var ritirato = false
        var avvRitirato = false

        giocatoriRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(listaGiocatori: DataSnapshot) {
                for (giocatore in listaGiocatori.children) {

                    if(giocatore.key.toString() != uid){
                        giocatore2esiste = true
                        avversario = giocatore.key.toString()
                        nomeAvv = giocatore.child("name").value.toString()
                    }

                    //SE MI SONO RITIRATO IO
                    if (giocatore.hasChild("ritirato") && giocatore.key.toString() == uid) {
                        ritirato = true
                    }
                    // SE SI è RITIRATO L'AVVERSARIO
                    if (giocatore.hasChild("ritirato") && giocatore.key.toString() != uid) {
                        avvRitirato = true
                    }


                    // A ENTRAMBI GIOCATORI, PER OGNI TOPIC PRENDO RISPOSTE CORRETTE
                    for (topic in giocatore.children) {

                        if (topic.hasChild("risposteTotali")) {

                            var giocatore1 = giocatore.key.toString()

                            // SE IO AGGIORNA MIE RISPOSTE
                            if (giocatore1 == uid) {
                                val risposteCorrette =
                                    topic.child("risposteCorrette").getValue(Int::class.java)
                                if (risposteCorrette != null) {
                                    risposte1 += risposteCorrette
                                }
                            }
                            //SE AVVERSARIO AGGIORNA SUE RISPOSTE
                            else {
                                val risposteCorrette =
                                    topic.child("risposteCorrette").getValue(Int::class.java)
                                if (risposteCorrette != null) {
                                    risposte2 += risposteCorrette
                                }

                            }
                        }
                    }


                }


                if (ritirato) {
                    giocatoriRef.child(uid).child("fineTurno").setValue("si")
                    //SE ANCORA NON SI CONNETTE NESSUNO CANCELLO LA PARTITA
                    if(!giocatore2esiste) {
                        GiocoUtils.spostaInPartiteTerminate(partita, modalita, difficolta, uid, risposte1, risposte2)
                    }
                }
                else if (avvRitirato){
                    giocatoriRef.child(uid).child("fineTurno").setValue("si")

                    GiocoUtils.spostaInPartiteTerminate(partita, modalita, difficolta, uid, risposte1, risposte2)
                    GiocoUtils.spostaInPartiteTerminate(partita, modalita, difficolta, avversario, risposte1, risposte2)

                    GiocoUtils.schermataVittoria(
                        requireActivity().supportFragmentManager,
                        R.id.fragmentContainerViewGioco2,
                        nomeAvv,
                        risposte1,
                        risposte2,
                        "argomento singolo"
                    )
                }
                else {
                    if (!giocatore2esiste) {
                        giocatoriRef.child(uid).child("fineTurno").setValue("si")
                        GiocoUtils.schermataAttendi(requireActivity().supportFragmentManager, R.id.fragmentContainerViewGioco2)
                    } else {
                        giocatoriRef.child(uid).child("fineTurno").setValue("si")
                        GiocoUtils.spostaInPartiteTerminate(partita, modalita, difficolta, uid, risposte1, risposte2)
                        GiocoUtils.spostaInPartiteTerminate(partita, modalita, difficolta, avversario, risposte1, risposte2)

                        when {
                            risposte1 > risposte2 -> GiocoUtils.schermataVittoria(requireActivity().supportFragmentManager, R.id.fragmentContainerViewGioco2, nomeAvv, risposte1, risposte2, "argomento singolo")
                            risposte1 == risposte2 -> GiocoUtils.schermataPareggio(requireActivity().supportFragmentManager, R.id.fragmentContainerViewGioco2, nomeAvv, risposte1, risposte2, "argomento singolo")
                            else -> GiocoUtils.schermataSconfitta(requireActivity().supportFragmentManager, R.id.fragmentContainerViewGioco2, nomeAvv, risposte1, risposte2, "argomento singolo")
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })


    }

    fun controllaRitiro() {
        database = FirebaseDatabase.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        var giocatoriRef =
            database.getReference("partite").child(modalita).child(difficolta).child(partita)
                .child("giocatori")
        giocatoriRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (giocatore in dataSnapshot.children) {

                    var giocatore1 = giocatore.key.toString()
                    if (giocatore.hasChild("ritirato")) {
                        if (giocatore1 != uid){
                            avvRitirato = true
                            finePartita()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }



    fun controllaRisposta(risposta:Button){
        if (!rispostaData) {

            if (GiocoUtils.QuestaèLaRispostaCorretta(risposta, rispostaCorretta)) {

                GiocoUtils.updateRisposte(risposteRef, "risposteCorrette")
                GiocoUtils.updateStatTopic(topic, "corretta")

            } else {

                GiocoUtils.updateRisposte(risposteRef, "risposteSbagliate")
                GiocoUtils.updateStatTopic(topic, "sbagliata")
            }

            //Controlliamo le risposte totali, se 10 finisce la partita senno prossima domanda
            modArgomentoActivity.contatoreRisposte++
            if (modArgomentoActivity.contatoreRisposte < 10) {
                modArgomentoActivity.getTriviaQuestion()
            } else {
                finePartita()
            }
            rispostaData = true

        }
    }
}