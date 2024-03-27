package com.example.opentrivia.statistiche

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import com.example.opentrivia.R
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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_stat, container, false)

        storia = view.findViewById(R.id.storia_arg)
        geografia = view.findViewById(R.id.geografia_arg)
        arte = view.findViewById(R.id.arte_arg)
        sport = view.findViewById(R.id.sport_arg)
        intrattenimento = view.findViewById(R.id.intrattenimento)
        scienze = view.findViewById(R.id.scienze_arg)

        storiaPerc = view.findViewById(R.id.percentualeStoria)
        geografiaPerc = view.findViewById(R.id.percentualeGeografia)
        artePerc = view.findViewById(R.id.percentualeArte)
        sportPerc = view.findViewById(R.id.percentualeSport)
        intrattenimentoPerc = view.findViewById(R.id.percentualeIntrattenimento)
        scienzePerc = view.findViewById(R.id.percentualeScienze)

        database = FirebaseDatabase.getInstance()

var uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
            var statRef = database.getReference("users").child(uid).child("stat")
            statRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(statistiche: DataSnapshot) {

                    Log.d("entraCallbackStat2", "si")
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
                            val context = activity?.applicationContext
                            val Text = context?.getString(R.string.argomento_percentuale, argomento, percentualeRiposte.toInt().toString())
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