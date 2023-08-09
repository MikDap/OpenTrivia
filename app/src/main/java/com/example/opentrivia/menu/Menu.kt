package com.example.opentrivia.menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation
import com.example.opentrivia.R


class Menu : Fragment() {
    private lateinit var startButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       val view = inflater.inflate(R.layout.menu, container, false)
startButton = view.findViewById(R.id.startButton)
        startButton.setOnClickListener { Navigation.findNavController(view).navigate(R.id.action_menu_to_modalita)}


        return view
    }

}