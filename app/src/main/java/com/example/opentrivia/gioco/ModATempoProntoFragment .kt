package com.example.opentrivia.gioco

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.opentrivia.R


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
        modATempoActivity.startAtempo()

    }



        return view
    }




    }





