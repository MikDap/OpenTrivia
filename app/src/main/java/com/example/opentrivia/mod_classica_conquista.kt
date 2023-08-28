package com.example.opentrivia

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button


class mod_classica_conquista : Fragment() {

    private lateinit var storiaButton: Button
    private lateinit var geografiaButton: Button
    private lateinit var arteButton: Button
    private lateinit var sportButton: Button
    private lateinit var intrattenimentoButton: Button
    private lateinit var scienzeButton: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.mod_classica_conquista, container, false)

        storiaButton = view.findViewById(R.id.storiabutton)
        geografiaButton = view.findViewById(R.id.geografiabutton)
        arteButton = view.findViewById(R.id.artebutton)
        sportButton = view.findViewById(R.id.sportbutton)
        intrattenimentoButton = view.findViewById(R.id.intrattenimentobutton)
        scienzeButton = view.findViewById(R.id.scienzebutton)

        storiaButton.setOnClickListener {

        }

        return view
    }


}