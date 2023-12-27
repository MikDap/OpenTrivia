package com.example.opentrivia.gioco.a_tempo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.opentrivia.R
import com.example.opentrivia.gioco.a_tempo.ModATempoActivity


class ModATempoProntoFragment : Fragment() {


    private lateinit var pronto: Button
    //private lateinit var timeProgressBar: TimeProgressBarView
private lateinit var modATempoActivity: ModATempoActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.mod_a_tempo_pronto, container, false)
        modATempoActivity=activity as ModATempoActivity
        pronto = view.findViewById(R.id.ready_button)


        pronto.setOnClickListener{
            if (modATempoActivity.sfidaAccettata == "false") {
                modATempoActivity.startAtempo()
            }
            else {
                modATempoActivity.getTriviaQuestion()
            }

    }



        return view
    }




    }





