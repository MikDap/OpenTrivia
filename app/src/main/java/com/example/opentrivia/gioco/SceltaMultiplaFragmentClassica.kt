package com.example.opentrivia.gioco

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
import androidx.fragment.app.Fragment
import com.example.opentrivia.MainActivity

import com.example.opentrivia.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class SceltaMultiplaFragmentClassica : Fragment() {

    private lateinit var database: FirebaseDatabase
    private lateinit var domanda: TextView
    private lateinit var risposta1: Button
    private lateinit var risposta2: Button
    private lateinit var risposta3: Button
    private lateinit var risposta4: Button


    private lateinit var modClassicaActivity: ModClassicaActivity
    private lateinit var rispostaCorretta: String


    private lateinit var partita: String
    private lateinit var modalita: String
    private lateinit var difficolta: String
    private lateinit var topic: String
    private  var contatoreRisposte = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.mod_classica_scelta_multipla, container, false)



        modClassicaActivity= activity as ModClassicaActivity

        domanda = view.findViewById(R.id.domanda)
        risposta1 = view.findViewById(R.id.risposta1)
        risposta2 = view.findViewById(R.id.risposta2)
        risposta3 = view.findViewById(R.id.risposta3)
        risposta4 = view.findViewById(R.id.risposta4)

        Log.d(modalita,"modalita")

            domanda.text = modClassicaActivity.domanda
            risposta1.text = modClassicaActivity.risposte[0]
            Log.d("risposta1", risposta1.text as String)
            risposta2.text = modClassicaActivity.risposte[1]
            risposta3.text = modClassicaActivity.risposte[2]
            risposta4.text = modClassicaActivity.risposte[3]
        rispostaCorretta = modClassicaActivity.rispostaCorretta

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        database = FirebaseDatabase.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        var risposteRef = database.getReference("partite").child(modalita).child(difficolta).child(partita)
            .child("giocatori").child(uid).child(topic)
        var partiteInCorsoRef = database.getReference("users").child(uid).child("partite in corso")


        var rispostaData = false
        risposta1.setOnClickListener {
            if (!rispostaData) {

                if (risposta1.text == rispostaCorretta) {
                    risposta1.setBackgroundColor(Color.LTGRAY)
                    Handler(Looper.getMainLooper()).postDelayed({
                        risposta1.setBackgroundColor(Color.GREEN)
                    }, 500)

                    updateRisposte(risposteRef,"corretta")



                } else {
                    risposta1.setBackgroundColor(Color.LTGRAY)
                    Handler(Looper.getMainLooper()).postDelayed({
                        risposta1.setBackgroundColor(Color.RED)
                    }, 500)

                    updateRisposte(risposteRef,"sbagliata")
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
                    }, 500)
                    updateRisposte(risposteRef,"corretta")

                } else {
                    risposta2.setBackgroundColor(Color.LTGRAY)
                    Handler(Looper.getMainLooper()).postDelayed({
                        risposta2.setBackgroundColor(Color.RED)
                    }, 500)
                    updateRisposte(risposteRef,"sbagliata")

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
                    updateRisposte(risposteRef,"corretta")

                } else {
                    risposta3.setBackgroundColor(Color.LTGRAY)
                    Handler(Looper.getMainLooper()).postDelayed({
                        risposta3.setBackgroundColor(Color.RED)
                    }, 500)
                    updateRisposte(risposteRef,"sbagliata")

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
                    updateRisposte(risposteRef,"corretta")

                } else {
                    risposta4.setBackgroundColor(Color.LTGRAY)
                    Handler(Looper.getMainLooper()).postDelayed({
                        risposta4.setBackgroundColor(Color.RED)
                    }, 500)
                    updateRisposte(risposteRef,"sbagliata")

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



                            if (contatoreRisposte < 3) {
                                modClassicaActivity.getTriviaQuestion()
                            } else {
                                startActivity(Intent(activity, MainActivity::class.java))

                            }


                    } else {
                        // Il dato non esiste nel database, quindi scrivi qualcosa
                        risposteRef.child("risposteTotali").setValue(1)
                        contatoreRisposte = 1
                        Log.d("contatoreRisposte", contatoreRisposte.toString())
                        modClassicaActivity.getTriviaQuestion()
                    }
                }

                else if (tipo == "sbagliata") {

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
                        if (contatoreRisposte < 3) {
                            modClassicaActivity.getTriviaQuestion()
                        }
                        else {startActivity(Intent(activity, MainActivity::class.java))

                        }

                    } else {
                        // Il dato non esiste nel database, quindi scrivi qualcosa
                        risposteRef.child("risposteTotali").setValue(1)
                        contatoreRisposte = 1
                        Log.d("contatoreRisposte", contatoreRisposte.toString())
                        modClassicaActivity.getTriviaQuestion()
                    }


                }



                //qua il codice per salvare sul database i dati nel nodo users per utilizzarli nella scrollview










            }


            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }



}

