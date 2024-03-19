package com.example.opentrivia.gioco.argomento_singolo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.opentrivia.R


class ArgomentoSingoloFragment : Fragment() {

    private lateinit var culturaPop: Button
    private lateinit var sport: Button
    private lateinit var storia: Button
    private lateinit var geografia: Button
    private lateinit var arte: Button
    private lateinit var scienze: Button

    lateinit var topic: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.mod_argomento_singolo_scelta_argomento, container, false)

        culturaPop = view.findViewById(R.id.intrattenimento_arg)
        sport = view.findViewById(R.id.sport_arg)
        storia = view.findViewById(R.id.storia_arg)
        geografia = view.findViewById(R.id.geografia_arg)
        arte = view.findViewById(R.id.arte_arg)
        scienze = view.findViewById(R.id.scienze_arg)
        val activity = requireActivity() as ModArgomentoActivity
        var argScelto = false

        culturaPop.setOnClickListener{
            if (!argScelto) {
                topic = "culturaPop"
                activity.topicScelto(topic)
                argScelto = true
            }
        }


        sport.setOnClickListener{
            if (!argScelto) {
                topic = "sport"
                activity.topicScelto(topic)
                argScelto = true
            }

        }

        storia.setOnClickListener{
            if (!argScelto) {
                topic = "storia"
                activity.topicScelto(topic)
                argScelto = true
            }

        }

        geografia.setOnClickListener{
            if (!argScelto) {
                topic = "geografia"
                activity.topicScelto(topic)
                argScelto = true
            }
        }
        scienze.setOnClickListener{
            if (!argScelto) {
                topic = "scienze"
                activity.topicScelto(topic)
                argScelto = true
            }
        }

        arte.setOnClickListener{
            if (!argScelto) {
                topic = "arte"
                activity.topicScelto(topic)
                argScelto = true
            }
        }

        return view
    }


}