package com.example.opentrivia.menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation
import com.example.opentrivia.R

class Difficolta() : Fragment() {
private lateinit var facileButton: Button
private lateinit var medioButton: Button
private lateinit var difficileButton: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // salviamo il bundle nella variabile modalita
        val modalita = arguments?.getString("modalita")

        // Inflate the layout for this fragment
       val view = inflater.inflate(R.layout.menu_difficolta, container, false)


        facileButton = view.findViewById(R.id.facileButton)
        facileButton.setOnClickListener {
            val bundle = Bundle()
            val difficolta = "easy"
            bundle.putString("modalita", modalita)
            bundle.putString("difficolta", difficolta)
            Navigation.findNavController(view).navigate(R.id.action_difficolta_to_sceltaGiocatore, bundle)
        }


        // collegamento difficolta-chiamata Api

        medioButton = view.findViewById(R.id.medioButton)
        medioButton.setOnClickListener {
            val bundle = Bundle()
            val difficolta = "medium"
            bundle.putString("modalita", modalita)
            bundle.putString("difficolta", difficolta)
            Navigation.findNavController(view).navigate(R.id.action_difficolta_to_sceltaGiocatore, bundle)
        }


        difficileButton = view.findViewById(R.id.difficileButton)
        difficileButton.setOnClickListener {
            val bundle = Bundle()
            val difficolta = "hard"
            bundle.putString("modalita", modalita)
            bundle.putString("difficolta", difficolta)
            Navigation.findNavController(view).navigate(R.id.action_difficolta_to_sceltaGiocatore, bundle)
           }




        return view
    }


}