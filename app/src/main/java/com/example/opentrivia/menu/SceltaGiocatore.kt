package com.example.opentrivia.menu

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.opentrivia.R
import com.example.opentrivia.gioco.ModATempoActivity
import com.example.opentrivia.gioco.ModArgomentoActivity
import com.example.opentrivia.gioco.ModClassicaActivity

class SceltaGiocatore : Fragment() {

    private lateinit var cercaPartitaButton: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val modalita = arguments?.getString("modalita")
        val difficolta = arguments?.getString("difficolta")



             // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.menu_scelta_giocatore, container, false)
        cercaPartitaButton = view.findViewById(R.id.cercaPartitaButton)
        cercaPartitaButton.setOnClickListener {


                    lateinit var intent: Intent
                    when (modalita) {
                        "classica" -> {intent = Intent(activity, ModClassicaActivity::class.java)}
                        "argomento singolo" -> {intent = Intent(activity, ModArgomentoActivity::class.java)}
                        "a tempo" -> {intent = Intent(activity, ModATempoActivity::class.java)}
                    }

                    intent.putExtra("difficolta",difficolta)
                    startActivity(intent)



        }

        return view


}
}