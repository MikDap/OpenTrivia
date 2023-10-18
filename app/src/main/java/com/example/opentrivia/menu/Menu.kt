package com.example.opentrivia.menu

import android.content.Intent
import android.os.Bundle
import android.text.Layout
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.Navigation
import com.example.opentrivia.R
import com.example.opentrivia.gioco.ModClassicaActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class Menu : Fragment() {
    private lateinit var startButton: Button
    private lateinit var partitaContainer: LinearLayout
    private lateinit var database: FirebaseDatabase
    private lateinit var giocaincorso: Button
    private lateinit var modClassicaActivity: ModClassicaActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       val view = inflater.inflate(R.layout.menu, container, false)
        val view2= inflater.inflate(R.layout.game_item_layout,container,false)
startButton = view.findViewById(R.id.startButton)
        partitaContainer = view.findViewById(R.id.linearLayout)
        giocaincorso=view2.findViewById(R.id.giocaincorso)
        database = FirebaseDatabase.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val partiteInCorsoRef = database.getReference("users").child(uid).child("partite in corso")
        val modalitaRef= database.getReference("partite").child("classica")
        partiteInCorsoRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(partiteInCorso: DataSnapshot) {

                if (partiteInCorso.hasChildren()) {
                    for (partita in partiteInCorso.children) {
            var difficolta=partita.child("difficolta").value.toString()
             //instanziare giocabutton
             leggiTurno(partita.toString(),difficolta,modalitaRef){
                 turno -> if(turno == uid ) {
                     giocaincorso.setOnClickListener(){
                         modClassicaActivity.partita= partita.toString()
                         var intent = Intent(activity, ModClassicaActivity::class.java)
                         startActivity(intent)
                     }
                 giocaincorso.visibility= View.VISIBLE

                 }
             }






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
                        //
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
    fun leggiTurno (partita : String, difficolta: String, modalitaRef: DatabaseReference,callback:(turno:String)-> Unit)
    {
        var turno : String =""
        modalitaRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(modalitaRef: DataSnapshot) {

                if (modalitaRef.child(difficolta).child(partita).hasChild("Turno")) {
                     turno= modalitaRef.child(difficolta).child(partita).child("Turno").toString()
                }

         callback(turno);
            }


            override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            }
        }
