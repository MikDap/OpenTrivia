package com.example.opentrivia.gioco

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
    private var listener: MyFragmentListener? = null

    lateinit var topic: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.mod_argomento_singolo_scelta_argomento, container, false)

        culturaPop = view.findViewById(R.id.intrattenimento_button)
        sport = view.findViewById(R.id.sport)
        storia = view.findViewById(R.id.storia)
        geografia = view.findViewById(R.id.geografia)
        arte = view.findViewById(R.id.arte)
        scienze = view.findViewById(R.id.scienze)

        culturaPop.setOnClickListener{
            topic = "culturaPop"
            passVariableToActivity(topic)
        }


        sport.setOnClickListener{
            topic = "sport"
            passVariableToActivity(topic)

        }

        storia.setOnClickListener{
            topic = "storia"
            passVariableToActivity(topic)

        }

        geografia.setOnClickListener{
            topic = "geografia"
            passVariableToActivity(topic)
        }
        scienze.setOnClickListener{
            topic = "scienze"
            passVariableToActivity(topic)
        }

        arte.setOnClickListener{
            topic = "arte"
            passVariableToActivity(topic)
        }

        return view
    }

    //crea un listener (funge da contratto tra il Fragment e la Activity)
    interface MyFragmentListener {
        //implementato in ModArgomentoActivity
        fun onVariablePassed(variable: String) {
        }
    }
//estrae l'Activity ospitante utilizzando requireActivity() e controlla che
// l'Activity implementi l'interfaccia MyFragmentListener.
// Quindi, chiama il metodo onVariablePassed
    fun passVariableToActivity(variable: String) {
        val activity = requireActivity() as MyFragmentListener
        activity.onVariablePassed(variable)
    }

//viene chiamato quando il Fragment viene associato all'Activity ospitante DA VEDERE SE LEVANDOLO, FUNZIONA
   override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MyFragmentListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement MyFragmentListener")
        }
    }




}