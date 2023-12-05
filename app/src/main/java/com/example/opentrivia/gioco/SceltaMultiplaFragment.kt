package com.example.opentrivia.gioco

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
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.opentrivia.MainActivity

import com.example.opentrivia.R
import com.example.opentrivia.StatisticheFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.system.exitProcess


class SceltaMultiplaFragment : Fragment() {

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =
            inflater.inflate(R.layout.mod_argomento_singolo_scelta_multipla, container, false)

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

        requireActivity().onBackPressedDispatcher.addCallback(callback)

        domanda = view.findViewById(R.id.domanda)
        risposta1 = view.findViewById(R.id.risposta1)
        risposta2 = view.findViewById(R.id.risposta2)
        risposta3 = view.findViewById(R.id.risposta3)
        risposta4 = view.findViewById(R.id.risposta4)

        Log.d(modalita, "modalita")


        domanda.text = modArgomentoActivity.domanda
        risposta1.text = modArgomentoActivity.risposte[0]
        Log.d("risposta1", risposta1.text as String)
        risposta2.text = modArgomentoActivity.risposte[1]
        risposta3.text = modArgomentoActivity.risposte[2]
        risposta4.text = modArgomentoActivity.risposte[3]
        rispostaCorretta = modArgomentoActivity.rispostaCorretta

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        database = FirebaseDatabase.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        var risposteRef =
            database.getReference("partite").child(modalita).child(difficolta).child(partita)
                .child("giocatori").child(uid).child(topic)
        var partiteInCorsoRef = database.getReference("users").child(uid).child("partite in corso")

             controllaRitiro()

        var rispostaData = false
        risposta1.setOnClickListener {
            if (!rispostaData) {

                if (risposta1.text == rispostaCorretta) {
                    risposta1.setBackgroundColor(Color.LTGRAY)
                    Handler(Looper.getMainLooper()).postDelayed({
                        risposta1.setBackgroundColor(Color.GREEN)
                    }, 1000)

                    updateRisposte(risposteRef, "corretta")
                    StatisticheFragment.updateStatTopic(topic, "corretta")


                } else {
                    risposta1.setBackgroundColor(Color.LTGRAY)
                    Handler(Looper.getMainLooper()).postDelayed({
                        risposta1.setBackgroundColor(Color.RED)
                    }, 1000)

                    updateRisposte(risposteRef, "sbagliata")
                    StatisticheFragment.updateStatTopic(topic, "sbagliata")
                }
                Log.d("contatoreRisposte2", contatoreRisposte.toString())
                rispostaData = true

            }
        }

        risposta2.setOnClickListener {
            if (!rispostaData) {
                if (risposta2.text == rispostaCorretta) {
                    risposta2.setBackgroundColor(Color.LTGRAY)
                    Handler(Looper.getMainLooper()).postDelayed({
                        risposta2.setBackgroundColor(Color.GREEN)
                    }, 1000)
                    updateRisposte(risposteRef, "corretta")
                    StatisticheFragment.updateStatTopic(topic, "corretta")

                } else {
                    risposta2.setBackgroundColor(Color.LTGRAY)
                    Handler(Looper.getMainLooper()).postDelayed({
                        risposta2.setBackgroundColor(Color.RED)
                    }, 1000)
                    updateRisposte(risposteRef, "sbagliata")
                    StatisticheFragment.updateStatTopic(topic, "sbagliata")

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
                    }, 1000)
                    updateRisposte(risposteRef, "corretta")
                    StatisticheFragment.updateStatTopic(topic, "corretta")

                } else {
                    risposta3.setBackgroundColor(Color.LTGRAY)
                    Handler(Looper.getMainLooper()).postDelayed({
                        risposta3.setBackgroundColor(Color.RED)
                    }, 1000)
                    updateRisposte(risposteRef, "sbagliata")
                    StatisticheFragment.updateStatTopic(topic, "sbagliata")

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
                    }, 1000)
                    updateRisposte(risposteRef, "corretta")
                    StatisticheFragment.updateStatTopic(topic, "corretta")

                } else {
                    risposta4.setBackgroundColor(Color.LTGRAY)
                    Handler(Looper.getMainLooper()).postDelayed({
                        risposta4.setBackgroundColor(Color.RED)
                    }, 1000)
                    updateRisposte(risposteRef, "sbagliata")
                    StatisticheFragment.updateStatTopic(topic, "sbagliata")

                }
                rispostaData = true
            }
        }


    }

    fun setParametriPartita(
        partita: String,
        modalita: String,
        difficolta: String,
        topic: String
    ) {
        // Imposta il valore della variabile partita come desiderato
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

                if (tipo == "corretta") {
                    if (dataSnapshot.child("risposteCorrette").exists()) {
                        // Il dato esiste nel database
                        var punti = dataSnapshot.child("risposteCorrette").value.toString().toInt()
                        punti++
                        risposteRef.child("risposteCorrette").setValue(punti)

                    } else {
                        // Il dato non esiste nel database, quindi scrivi qualcosa
                        risposteRef.child("risposteCorrette").setValue(1)
                    }


                    if (dataSnapshot.child("risposteTotali").exists()) {
                        // Il dato esiste nel database
                        var punti = dataSnapshot.child("risposteTotali").value.toString().toInt()
                        punti++
                        contatoreRisposte = punti

                        Log.d("contatoreRisposte", contatoreRisposte.toString())
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
                        Log.d("contatoreRisposte", contatoreRisposte.toString())
                        modArgomentoActivity.getTriviaQuestion()
                    }
                } else if (tipo == "sbagliata") {

                    if (dataSnapshot.child("risposteSbagliate").exists()) {
                        // Il dato esiste nel database
                        var punti = dataSnapshot.child("risposteSbagliate").value.toString().toInt()
                        punti++
                        risposteRef.child("risposteSbagliate").setValue(punti)

                    } else {
                        // Il dato non esiste nel database, quindi scrivi qualcosa
                        risposteRef.child("risposteSbagliate").setValue(1)
                    }


                    if (dataSnapshot.child("risposteTotali").exists()) {
                        // Il dato esiste nel database
                        var punti = dataSnapshot.child("risposteTotali").value.toString().toInt()
                        punti++
                        contatoreRisposte = punti
                        Log.d("contatoreRisposte", contatoreRisposte.toString())
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
                        Log.d("contatoreRisposte", contatoreRisposte.toString())
                        modArgomentoActivity.getTriviaQuestion()
                    }


                }


                //qua il codice per salvare sul database i dati nel nodo users per utilizzarli nella scrollview


            }


            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    //modifica di mic

    fun finePartita() {
        Log.d("finePartita", "si")
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
                                avversario = giocatore1
                                nomeAvv = giocatore.child("name").value.toString()
                                giocatore2esiste = true
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
                    modArgomentoActivity.schermataSconfitta(nomeAvv, risposte1, risposte2)
                } else if (ritirato) {
                    giocatoriRef.child(uid).child("fineTurno").setValue("si")
                    StatisticheFragment.StatisticheTerminate(
                        partita,
                        modalita,
                        difficolta,
                        uid,
                        risposte1,
                        risposte2
                    )
                    modArgomentoActivity.schermataSconfitta(nomeAvv, risposte1, risposte2)
                }
                else if (avvRitirato){
                    giocatoriRef.child(uid).child("fineTurno").setValue("si")
                    StatisticheFragment.StatisticheTerminate(
                        partita,
                        modalita,
                        difficolta,
                        uid,
                        risposte1,
                        risposte2
                    )
                    StatisticheFragment.StatisticheTerminate(
                        partita,
                        modalita,
                        difficolta,
                        avversario,
                        risposte1,
                        risposte2
                    )
                    modArgomentoActivity.schermataVittoria(nomeAvv, risposte1, risposte2)
                }
                else {
                    Log.d("giocatore2esiste alla fine", giocatore2esiste.toString())
                    if (!giocatore2esiste) {
                        giocatoriRef.child(uid).child("fineTurno").setValue("si")
                        modArgomentoActivity.schermataAttendi()
                    } else {
                        Log.d("entra in else", "si")
                        if (risposte1 > risposte2) {

                            giocatoriRef.child(uid).child("fineTurno").setValue("si")
                            StatisticheFragment.StatisticheTerminate(
                                partita,
                                modalita,
                                difficolta,
                                uid,
                                risposte1,
                                risposte2
                            )
                            StatisticheFragment.StatisticheTerminate(
                                partita,
                                modalita,
                                difficolta,
                                avversario,
                                risposte1,
                                risposte2
                            )
                            modArgomentoActivity.schermataVittoria(nomeAvv, risposte1, risposte2)

                        } else if (risposte1 == risposte2) {

                            giocatoriRef.child(uid).child("fineTurno").setValue("si")
                            StatisticheFragment.StatisticheTerminate(
                                partita,
                                modalita,
                                difficolta,
                                uid,
                                risposte1,
                                risposte2
                            )
                            StatisticheFragment.StatisticheTerminate(
                                partita,
                                modalita,
                                difficolta,
                                avversario,
                                risposte1,
                                risposte2
                            )
                            modArgomentoActivity.schermataPareggio(nomeAvv, risposte1, risposte2)

                        } else if (risposte1 < risposte2) {

                            giocatoriRef.child(uid).child("fineTurno").setValue("si")
                            StatisticheFragment.StatisticheTerminate(
                                partita,
                                modalita,
                                difficolta,
                                uid,
                                risposte1,
                                risposte2
                            )
                            StatisticheFragment.StatisticheTerminate(
                                partita,
                                modalita,
                                difficolta,
                                avversario,
                                risposte1,
                                risposte2
                            )
                            modArgomentoActivity.schermataSconfitta(nomeAvv, risposte1, risposte2)

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
}