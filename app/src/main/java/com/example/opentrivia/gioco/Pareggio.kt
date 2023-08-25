package com.example.opentrivia.gioco

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.opentrivia.R


class Pareggio : Fragment() {
    lateinit var pareggio:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pareggio, container, false)

       //pareggio = (view?.findViewById(R.id.congratulazioni) ?: pareggio.setBackgroundColor(Color.LTGRAY )) as TextView
        //Handler(Looper.getMainLooper()).postDelayed({
          //  pareggio.setBackgroundColor(Color.RED)
        //}, 1000)
    }


}