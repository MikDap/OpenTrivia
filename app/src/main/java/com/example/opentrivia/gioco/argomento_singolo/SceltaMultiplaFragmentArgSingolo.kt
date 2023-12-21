package com.example.opentrivia.gioco.argomento_singolo

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
    private var contatoreRisposte = 0

    var rispostaData = false
    private lateinit var uid:String
    private lateinit var risposteRef: DatabaseReference

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
         risposteRef =
            database.getReference("partite").child(modalita).child(difficolta).child(partita)
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


    fun updateRisposte(
        risposteRef: DatabaseReference, tipo: String,
    ) {
        risposteRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                    if (dataSnapshot.child(tipo).exists()) {
                        // Il dato esiste nel database
                        var punti = dataSnapshot.child(tipo).value.toString().toInt()
                        punti++
                        risposteRef.child(tipo).setValue(punti)
                    } else {
                        // Il dato non esiste nel database
                        risposteRef.child(tipo).setValue(1)
                    }
                    if (dataSnapshot.child("risposteTotali").exists()) {
                        // Il dato esiste nel database
                        var punti = dataSnapshot.child("risposteTotali").value.toString().toInt()
                        punti++
                        contatoreRisposte = punti

                        risposteRef.child("risposteTotali").setValue(punti)

                        if (contatoreRisposte < 10) {
                            modArgomentoActivity.getTriviaQuestion()
                        } else {
                            finePartita()

                        }
                    } else {
                        // Il dato non esiste nel database, quindi scrivi qualcosa
                        risposteRef.child("risposteTotali").setValue(1)
                        contatoreRisposte = 1
                        modArgomentoActivity.getTriviaQuestion()
                    }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
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
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (giocatore in dataSnapshot.children) {

                    if(giocatore.key.toString() != uid){
                        giocatore2esiste = true
                        avversario = giocatore.key.toString()
                        nomeAvv = giocatore.child("name").value.toString()
                    }

                    if (giocatore.hasChild("ritirato") && giocatore.key.toString() == uid) {
                        ritirato = true
                    }
                    if (giocatore.hasChild("ritirato") && giocatore.key.toString() != uid) {
                        avvRitirato = true
                    }
                    for (topic in giocatore.children) {

                        if (topic.hasChild("risposteTotali")) {


                            var giocatore1 = giocatore.key.toString()

                            Log.d("giocatore1", giocatore1)
                            Log.d("uid", uid)
                            if (giocatore1 == uid) {
                                Log.d("giocatore2esiste", giocatore2esiste.toString())
                                val risposteCorrette =
                                    topic.child("risposteCorrette").getValue(Int::class.java)
                                if (risposteCorrette != null) {
                                    risposte1 += risposteCorrette
                                }
                            } else {
                                Log.d("giocatore2esiste", giocatore2esiste.toString())
                                val risposteCorrette =
                                    topic.child("risposteCorrette").getValue(Int::class.java)
                                if (risposteCorrette != null) {
                                    risposte2 += risposteCorrette
                                }

                            }
                        }
                    }


                }






                if (ritirato && giocatore2esiste) {
                    giocatoriRef.child(uid).child("fineTurno").setValue("si")
                    Log.d("entra1", "si")
                } else if (ritirato) {
                    Log.d("entra2", "si")
                    giocatoriRef.child(uid).child("fineTurno").setValue("si")
                    GiocoUtils.spostaInPartiteTerminate(
                        partita,
                        modalita,
                        difficolta,
                        uid,
                        risposte1,
                        risposte2
                    )
                }
                else if (avvRitirato){
                    giocatoriRef.child(uid).child("fineTurno").setValue("si")
                    GiocoUtils.spostaInPartiteTerminate(
                        partita,
                        modalita,
                        difficolta,
                        uid,
                        risposte1,
                        risposte2
                    )
                    GiocoUtils.spostaInPartiteTerminate(
                        partita,
                        modalita,
                        difficolta,
                        avversario,
                        risposte1,
                        risposte2
                    )
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
                    Log.d("giocatore2esiste alla fine", giocatore2esiste.toString())
                    if (!giocatore2esiste) {
                        giocatoriRef.child(uid).child("fineTurno").setValue("si")
                        GiocoUtils.schermataAttendi(requireActivity().supportFragmentManager, R.id.fragmentContainerViewGioco2)
                    } else {
                        Log.d("entra in else", "si")
                        if (risposte1 > risposte2) {

                            giocatoriRef.child(uid).child("fineTurno").setValue("si")
                            GiocoUtils.spostaInPartiteTerminate(
                                partita,
                                modalita,
                                difficolta,
                                uid,
                                risposte1,
                                risposte2
                            )
                            GiocoUtils.spostaInPartiteTerminate(
                                partita,
                                modalita,
                                difficolta,
                                avversario,
                                risposte1,
                                risposte2
                            )
                            GiocoUtils.schermataVittoria(
                                requireActivity().supportFragmentManager,
                                R.id.fragmentContainerViewGioco2,
                                nomeAvv,
                                risposte1,
                                risposte2,
                                "argomento singolo"
                            )

                        } else if (risposte1 == risposte2) {

                            giocatoriRef.child(uid).child("fineTurno").setValue("si")
                            GiocoUtils.spostaInPartiteTerminate(
                                partita,
                                modalita,
                                difficolta,
                                uid,
                                risposte1,
                                risposte2
                            )
                            GiocoUtils.spostaInPartiteTerminate(
                                partita,
                                modalita,
                                difficolta,
                                avversario,
                                risposte1,
                                risposte2
                            )
                            GiocoUtils.schermataPareggio(
                                requireActivity().supportFragmentManager,
                                R.id.fragmentContainerViewGioco2,
                                nomeAvv,
                                risposte1,
                                risposte2,
                                "argomento singolo"
                            )

                        } else if (risposte1 < risposte2) {

                            giocatoriRef.child(uid).child("fineTurno").setValue("si")
                            GiocoUtils.spostaInPartiteTerminate(
                                partita,
                                modalita,
                                difficolta,
                                uid,
                                risposte1,
                                risposte2
                            )
                            GiocoUtils.spostaInPartiteTerminate(
                                partita,
                                modalita,
                                difficolta,
                                avversario,
                                risposte1,
                                risposte2
                            )


                            GiocoUtils.schermataSconfitta(
                                requireActivity().supportFragmentManager,
                                R.id.fragmentContainerViewGioco2,
                                nomeAvv,
                                risposte1,
                                risposte2,
                                "argomento singolo"
                            )

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

                updateRisposte(risposteRef, "risposteCorrette")
                GiocoUtils.updateStatTopic(topic, "corretta")

            } else {

                updateRisposte(risposteRef, "risposteSbagliate")
                GiocoUtils.updateStatTopic(topic, "sbagliata")
            }

            rispostaData = true

        }
    }
}