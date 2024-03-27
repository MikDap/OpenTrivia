package com.example.opentrivia.menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation
import com.example.opentrivia.R


class Modalita : Fragment() {
private lateinit var classicaButton: Button
    private lateinit var tempoButton: Button
    private lateinit var argomentoButton: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       val view = inflater.inflate(R.layout.menu_modalita, container, false)
        classicaButton = view.findViewById(R.id.classicaButton)
        classicaButton.setOnClickListener {
            //vogliamo passare alla classe difficoltà il nome della modalità
            val bundle = Bundle()
            val modalita = "classica"
            bundle.putString("modalita", modalita)
            //navighiamo alla schermata della difficoltà, passando il bundle
            Navigation.findNavController(view).navigate(R.id.action_modalita_to_difficolta, bundle)
        }

        tempoButton = view.findViewById(R.id.tempoButton)
        tempoButton.setOnClickListener {
            val bundle = Bundle()
            val modalita = "a tempo"
            bundle.putString("modalita", modalita)
            Navigation.findNavController(view).navigate(R.id.action_modalita_to_difficolta, bundle)}

        argomentoButton = view.findViewById(R.id.argomentoButton)
        argomentoButton.setOnClickListener {
            val bundle = Bundle()
            val modalita = "argomento singolo"
            bundle.putString("modalita", modalita)
            Navigation.findNavController(view).navigate(R.id.action_modalita_to_difficolta, bundle)}

        return view
    }


}