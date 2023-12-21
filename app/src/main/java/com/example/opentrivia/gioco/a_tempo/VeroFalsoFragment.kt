package com.example.opentrivia.gioco.a_tempo
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.opentrivia.R
import com.example.opentrivia.utils.GiocoUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
class VeroFalsoFragment : Fragment() {
    private lateinit var database: FirebaseDatabase
    private lateinit var domanda: TextView
    private lateinit var risposta1: Button
    private lateinit var risposta2: Button
    //private lateinit var risposta3: Button
    //private lateinit var risposta4: Button
    private lateinit var modATempoActivity: ModATempoActivity
    private lateinit var rispostaCorretta: String
    lateinit var partita: String
    lateinit var modalita: String
    lateinit var difficolta: String
    lateinit var topic: String
    private  var contatoreRisposte = 0
    var timeStamp: Long = 0L
    var finalTime: Long = 60000L
    lateinit var progressBarView: TimeProgressBarView
    //1. in ModATempoActivity creiamo l'istanza di questa classe col tempo settato (la prima volta a 0)
    //attraverso la funzione newInstance.
    //2. Nell'OnViewCreated passiamo il tempo preso tramite newInstance alla TimeProgressBar.
    //3. Quando l'utente dà la risposta (vero o falso), salviamo il tempo trascorso nella TimeProgressBar
    // e lo salviamo nella variabile di classe elapsedTimeInMillis.
    //4. Chiamiamo la funzione passElapsedTime per passare elapsedTimeInMillis a ModATempoActivity.
    //5. Riprende dal punto 1.


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        modATempoActivity= activity as ModATempoActivity
        timeStamp = modATempoActivity.timeStamp
        finalTime = modATempoActivity.finalTime
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_vero_falso, container, false)

        domanda = view.findViewById(R.id.domandavf)
        risposta1 = view.findViewById(R.id.vfrisposta1)
        risposta2 = view.findViewById(R.id.vfrisposta2)
        Log.d(modalita,"modalita")
        domanda.text = modATempoActivity.domanda
        risposta1.text = "True"
        Log.d("risposta1", risposta1.text as String)
        risposta2.text = "False"
        //  risposta3.text = modATempoActivity.risposte[2]
        // risposta4.text = modATempoActivity.risposte[3]
        rispostaCorretta = modATempoActivity.rispostaCorretta
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val timeProgressBarView = view.findViewById<TimeProgressBarView>(R.id.timeProgressBar)
        timeProgressBarView.timeStamp = timeStamp
        timeProgressBarView.finalTime = finalTime
        timeProgressBarView.associatedFragment = this
        timeProgressBarView.startTimer()
        progressBarView = timeProgressBarView
        database = FirebaseDatabase.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        var risposteRef = database.getReference("partite").child(modalita).child(difficolta).child(partita)
            .child("giocatori").child(uid).child(topic)
        var partiteInCorsoRef = database.getReference("users").child(uid).child("partite in corso")
        var rispostaData = false
        risposta1.setOnClickListener {
            if (!rispostaData) {
                if (risposta1.text == rispostaCorretta) {
                    risposta1.setBackgroundColor(Color.GREEN)
                    updateRisposte(risposteRef,"corretta")
                    GiocoUtils.updateStatTopic(topic,"corretta")
                } else {
                    risposta1.setBackgroundColor(Color.RED)
                    updateRisposte(risposteRef,"sbagliata")
                    GiocoUtils.updateStatTopic(topic,"sbagliata")
                }
                Log.d("contatoreRisposte2", contatoreRisposte.toString())
                rispostaData = true
            }
        }
        risposta2.setOnClickListener {
            if (!rispostaData) {
                if (risposta2.text == rispostaCorretta) {
                    risposta2.setBackgroundColor(Color.GREEN)
                    updateRisposte(risposteRef,"corretta")
                    GiocoUtils.updateStatTopic(topic,"corretta")
                } else {
                    risposta2.setBackgroundColor(Color.RED)
                    updateRisposte(risposteRef,"sbagliata")
                    GiocoUtils.updateStatTopic(topic,"sbagliata")
                }
                rispostaData = true
            }
        }
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
                        modATempoActivity.getTriviaQuestion()
                    } else {
                        // Il dato non esiste nel database, quindi scrivi qualcosa
                        risposteRef.child("risposteTotali").setValue(1)
                        contatoreRisposte = 1
                        Log.d("contatoreRisposte", contatoreRisposte.toString())
                        modATempoActivity.getTriviaQuestion()
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
                        //    if (contatoreRisposte < 10) {
                        modATempoActivity.getTriviaQuestion()
                        //         }
                        //         else {startActivity(Intent(activity, MainActivity::class.java))
                        //           }
                    } else {
                        // Il dato non esiste nel database, quindi scrivi qualcosa
                        risposteRef.child("risposteTotali").setValue(1)
                        contatoreRisposte = 1
                        Log.d("contatoreRisposte", contatoreRisposte.toString())
                        modATempoActivity.getTriviaQuestion()
                    }
                }
                //qua il codice per salvare sul database i dati nel nodo users per utilizzarli nella scrollview


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
            database.getReference("partite").child(modalita).child(difficolta).child(partita).child("giocatori")
        var risposte1 = 0
        var risposte2 = 0
        var giocatore2esiste = false
        var avversario = ""
        var nomeAvv = ""
        giocatoriRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (giocatore in dataSnapshot.children) {
                    for (topic in giocatore.children) {
                        if (topic.hasChild("risposteCorrette")) {
                            var giocatore1 = giocatore.key.toString()
                            if (giocatore1.equals(uid)) {
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


                Log.d("giocatore2esiste alla fine", giocatore2esiste.toString())
                if (giocatore2esiste == false) {
                    giocatoriRef.child(uid).child("fineTurno").setValue("si")
                    GiocoUtils.schermataAttendi(requireActivity().supportFragmentManager, R.id.fragmentContainerViewGioco3)
                } else {
                    Log.d("entra in else", "si")
                    if (risposte1 > risposte2) {
                        var fineTurno = giocatoriRef.child(uid).child("fineTurno").setValue("si")
                        fineTurno.addOnCompleteListener {
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
                                R.id.fragmentContainerViewGioco3,
                                nomeAvv,
                                risposte1,
                                risposte2,
                                "a tempo"
                            )
                        }
                    } else if (risposte1 == risposte2) {
                        var fineTurno = giocatoriRef.child(uid).child("fineTurno").setValue("si")
                        fineTurno.addOnCompleteListener {
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
                                R.id.fragmentContainerViewGioco3,
                                nomeAvv,
                                risposte1,
                                risposte2,
                                "a tempo"
                            )
                        }
                    } else if (risposte1 < risposte2) {
                        var fineTurno = giocatoriRef.child(uid).child("fineTurno").setValue("si")
                        fineTurno.addOnCompleteListener {
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
                                R.id.fragmentContainerViewGioco3,
                                nomeAvv,
                                risposte1,
                                risposte2,
                                "a tempo"
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

    override fun onDestroyView() {
        super.onDestroyView()
        progressBarView.stopTimer()
    }
}
 