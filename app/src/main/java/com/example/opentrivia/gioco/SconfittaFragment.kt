package com.example.opentrivia.gioco

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.opentrivia.menu.MenuActivity
import com.example.opentrivia.R


class SconfittaFragment : Fragment() {

    private lateinit var NomeAvversario: TextView
    private lateinit var scoreTextView1: TextView
    private lateinit var scoreTextView3: TextView
    private lateinit var modalita: TextView
    lateinit var nomeAvv: String
    lateinit var scoreMio: String
    lateinit var scoreAvv: String
    lateinit var mod: String
    lateinit var menu: Button
    lateinit var intent:Intent


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_sconfitta, container, false)


        NomeAvversario = view.findViewById(R.id.NomeAvversario)
        scoreTextView1 = view.findViewById(R.id.scoreTextView1)
        scoreTextView3 = view.findViewById(R.id.scoreTextView3)
        modalita = view.findViewById(R.id.modalita)
        menu = view.findViewById(R.id.esci)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        NomeAvversario.text = nomeAvv
        scoreTextView1.text = scoreMio
        scoreTextView3.text = scoreAvv
        modalita.text = mod
        menu.setOnClickListener {
            intent = Intent(activity, MenuActivity::class.java)
            startActivity(intent)

        }
    }
}