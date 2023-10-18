package com.example.opentrivia.menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.Navigation
import com.example.opentrivia.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class Menu : Fragment() {
    private lateinit var startButton: Button
    private lateinit var partitaContainer: LinearLayout
    private lateinit var database: FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       val view = inflater.inflate(R.layout.menu, container, false)
startButton = view.findViewById(R.id.startButton)
        partitaContainer = view.findViewById(R.id.linearLayout)
        database = FirebaseDatabase.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val partiteInCorsoRef = database.getReference("users").child(uid).child("partite in corso")


        partiteInCorsoRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(partiteInCorso: DataSnapshot) {

                if (partiteInCorso.hasChildren()) {
                    for (partita in partiteInCorso.children) {


                        // FAI FUNZIONE CHE LEGGE TURNO DAL DATABASE, DA METTERE FUORI SEMPRE IN QUESTA CLASSE
                        // (vedi in modClassicaUtils come potresti farla)
                        // (trova partitaRef, il turno sta in partitaRef.child("Turno");
                        //
                        //
                        //   SCRIVERE IN QUESTA FUNZIONE (for partita) tutto il codice sotto:
                        //
                        //  1- chiami qui la funzione di sopra
                        //
                        //
                        //
                        //       (IL TURNO PRESO SOPRA LO CONFRONTI)
                        //  2- SE (TURNO == UID)  {

                        //  ---    GIOCABUTTON.listener DEVE COLLEGARMI ALLA PARTITA:
                        //               istanziare MODCLASSICAACTIITY
                        //       modClassicaActivity.partita = partita
                        //       passare a modclassicaactivty
                        //
                        //  ---   TROVARE ID SFONDO PULSANTE E CAMBIARE COLORE
                        // }
                        //
                        val gameView = inflater.inflate(R.layout.game_item_layout, partitaContainer, false)


                        val opponentNameTextView = gameView.findViewById<TextView>(R.id.opponentNameTextView)
                        val scoremeTextView = gameView.findViewById<TextView>(R.id.scoreme)
                        val scoreavversarioTextView = gameView.findViewById<TextView>(R.id.scoreavversario)
                        val giocaButton = gameView.findViewById<TextView>(R.id.giocaincorso)


                        var avversario = partita.child("Avversario").value.toString()
                        var punteggioMio = partita.child("PunteggioMio").value.toString()
                        var punteggioAvversario = partita.child("PunteggioAvversario").value.toString()

                        scoremeTextView.text = punteggioMio
                        opponentNameTextView.text = avversario
                        scoreavversarioTextView.text = punteggioAvversario

                        partitaContainer.addView(gameView)


                    }
                }






            }








            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })





        startButton.setOnClickListener { Navigation.findNavController(view).navigate(R.id.action_menu_to_modalita)}


        return view
    }

}