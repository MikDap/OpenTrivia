package com.example.opentrivia.gioco

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.opentrivia.menu.MenuActivity
import com.example.opentrivia.R

class AttendiTurnoFragment : Fragment() {
    lateinit var menu: Button
    lateinit var intent: Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_attendi_turno, container, false)
        menu = view.findViewById(R.id.esci)

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        menu.setOnClickListener {
          intent=Intent(activity, MenuActivity::class.java)
            startActivity(intent)

    }
}
}