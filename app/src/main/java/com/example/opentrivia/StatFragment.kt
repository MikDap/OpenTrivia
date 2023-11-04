package com.example.opentrivia

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class StatFragment : Fragment() {

    private lateinit var storia: TextView
    private lateinit var storiaPerc: Guideline
    private lateinit var geografia: TextView
    private lateinit var geografiaPerc: Guideline
    private lateinit var arte: TextView
    private lateinit var artePerc: Guideline
    private lateinit var sport: TextView
    private lateinit var sportPerc: Guideline
    private lateinit var intrattenimento: TextView
    private lateinit var intrattenimentoPerc: Guideline
    private lateinit var scienze: TextView
    private lateinit var scienzePerc: Guideline
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_stat, container, false)

        storia = view.findViewById(R.id.storia)
        geografia = view.findViewById(R.id.geografia)
        arte = view.findViewById(R.id.arte)
        sport = view.findViewById(R.id.sport)
        intrattenimento = view.findViewById(R.id.intrattenimento)
        scienze = view.findViewById(R.id.scienze)

        storiaPerc = view.findViewById(R.id.percentualeStoria)
        geografiaPerc = view.findViewById(R.id.percentualeGeografia)
        artePerc = view.findViewById(R.id.percentualeArte)
        sportPerc = view.findViewById(R.id.percentualeSport)
        intrattenimentoPerc = view.findViewById(R.id.percentualeIntrattenimento)
        scienzePerc = view.findViewById(R.id.percentualeScienze)

        updateStatistiche()

        var uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        database = FirebaseDatabase.getInstance()
        var statRef = database.getReference("users").child(uid).child("stat")


        statRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(statistiche: DataSnapshot) {


                for (topic1 in statistiche.children) {

                    var argomento = topic1.key.toString()
                        controllaTopic(argomento){
                            topic, topicPerc ->

                            var percentualeRiposte =
                                topic1.child("%risposteCorrette").value.toString().toFloat()

                            var floatPerc = percentualeRiposte / 100

                            val params = topicPerc.layoutParams as ConstraintLayout.LayoutParams
                            params.guidePercent = floatPerc

                            topicPerc.layoutParams = params

                            val Text = getString(R.string.argomento_percentuale, argomento, percentualeRiposte.toInt().toString())
                            topic.text = Text

                        }

                }


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        return view
    }


    // partiteTerminate/modalita/idPartita/
    // esito(lo calcoliamo dopo) - io - avversario/               <- argomenti conquistati
    // topic/
    //  arte../risposte corr-sba-tot

    //NELL'ONCREATE DEL FRAGMENT :
    fun updateStatistiche() {
        var uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        database = FirebaseDatabase.getInstance()
        var partiteTerminateRef =
            database.getReference("users").child(uid).child("partite terminate")
        partiteTerminateRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(partiteTerminate: DataSnapshot) {
                for (modalita in partiteTerminate.children) {
                    for (difficolta in modalita.children)
                    for (partita in difficolta.children) {
                        val uidSnapshot = partita.child("giocatori").child(uid)
                        for (topic in uidSnapshot.children) {
                            if(topic.hasChild("risposteTotali")) {
                                var topicNome = topic.key.toString()
                                var risposteCorrette = 0
                                if (topic.hasChild("risposteCorrette")) {
                                     risposteCorrette =
                                        topic.child("risposteCorrette").value.toString().toInt()
                                }
                                var risposteTotali =
                                    topic.child("risposteTotali").value.toString().toInt()
                                updateStatisticheTopic(
                                    topicNome,
                                    risposteCorrette,
                                    risposteTotali
                                )
                            }

                        }
                    }
                }
            }


            override fun onCancelled(error: DatabaseError) {
                // Gestisci l'errore, se necessario
            }
        })
    }
//      users/uid/stat/topic/
//  /risposte corrette-sbagliate-totali  % risposte corrette

    fun updateStatisticheTopic(
        topic: String,
        risposteCorrette: Int,
        risposteTotali: Int
    ) {
        database = FirebaseDatabase.getInstance()
        var uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        var statRef = database.getReference("users").child(uid).child("stat")
        statRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(statistiche: DataSnapshot) {


                if (statistiche.child(topic).hasChild("risposteTotali")) {
                    var risposteCorretteDatabase = 0F
                    if (statistiche.child(topic).hasChild("risposteCorrette")) {
                         risposteCorretteDatabase = statistiche.child(topic).child("risposteCorrette").value.toString().toFloat()
                    }
                    var risposteTotaliDatabase =
                        statistiche.child(topic).child("risposteTotali").value.toString().toFloat()


                    var risposteCorretteAggiornate = risposteCorretteDatabase + risposteCorrette
                    var risposteTotaliAggiornate = risposteTotaliDatabase + risposteTotali

                    //aggiorno la percentuale delle risposte corrette
                    var risposteCorrettePercentuale =
                        (risposteCorretteAggiornate * 100 / risposteTotaliAggiornate)
                    //e la setto sul database
                    statRef.child(topic).child("%risposteCorrette")
                        .setValue(risposteCorrettePercentuale)
                }
                else {


                    var risposteCorretteAggiornate =risposteCorrette.toFloat()
                    var risposteTotaliAggiornate = risposteTotali.toFloat()

                    //aggiorno la percentuale delle risposte corrette
                    var risposteCorrettePercentuale =
                        (risposteCorretteAggiornate * 100 / risposteTotaliAggiornate)
                    //e la setto sul database
                    statRef.child(topic).child("%risposteCorrette")
                        .setValue(risposteCorrettePercentuale)
                    statRef.child(topic).child("risposteCorrette")
                        .setValue(risposteCorretteAggiornate)
                    statRef.child(topic).child("risposteTotali")
                        .setValue(risposteTotaliAggiornate)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


    fun controllaTopic(
        argomento: String,
        callback: (topic: TextView, topicPerc: Guideline) -> Unit
    ) {
var topic= storia
var topicPerc= storiaPerc

        when (argomento) {

            "storia" -> {
                 topic = storia
                 topicPerc = storiaPerc
            }

            "sport" -> {
                topic = sport
                 topicPerc = sportPerc
            }

            "geografia" -> {
                 topic = geografia
                 topicPerc = geografiaPerc
            }

            "arte" -> {
                 topic = arte
                 topicPerc = artePerc
            }

            "scienze" -> {
                 topic = scienze
                 topicPerc = scienzePerc
            }

            "culturaPop" -> {
                 topic = intrattenimento
                 topicPerc = intrattenimentoPerc
            }

        }

        callback(topic, topicPerc)

    }






}