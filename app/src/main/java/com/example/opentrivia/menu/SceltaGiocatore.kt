package com.example.opentrivia.menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation
import com.example.opentrivia.R


class SceltaGiocatore : Fragment() {

    private lateinit var casualeButton: Button
    private lateinit var amicoButton: Button
    private lateinit var continuaButton: Button

    private lateinit var modalita: String
    private lateinit var difficolta: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val modalita = arguments?.getString("modalita")
        val difficolta = arguments?.getString("difficolta")
        if (modalita != null) {
            this.modalita = modalita
        }
        if (difficolta != null) {
            this.difficolta = difficolta
        }

        val view = inflater.inflate(R.layout.menu_scelta_giocatore, container, false)


        casualeButton = view.findViewById(R.id.casualeButton)
        amicoButton = view.findViewById(R.id.amicoButton)
        continuaButton = view.findViewById(R.id.continuaButton)

        return view
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var selezione = "nessuno"

        casualeButton.setOnClickListener {
            selezione = "casuale"
            casualeButton.setBackgroundResource(R.drawable.custom_border_orange)
            amicoButton.setBackgroundResource(R.drawable.custom_border_pareggio)
        }

        amicoButton.setOnClickListener{
            selezione = "amico"
            casualeButton.setBackgroundResource(R.drawable.custom_border_pareggio)
            amicoButton.setBackgroundResource(R.drawable.custom_border_orange)
        }

        continuaButton.setOnClickListener {

            val bundle = Bundle()
            bundle.putString("modalita", modalita)
            bundle.putString("difficolta", difficolta)

            if (selezione == "casuale"){
                bundle.putString("selezione", "casuale")
            }
            if (selezione == "amico"){
                bundle.putString("selezione", "amico")
            }

            Navigation.findNavController(view).navigate(R.id.action_sceltaGiocatore_to_iniziaPartita, bundle)
        }

    }

}