package com.example.opentrivia

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.example.opentrivia.gioco.a_tempo.ModATempoActivity
import com.example.opentrivia.gioco.argomento_singolo.ModArgomentoActivity
import com.example.opentrivia.gioco.classica.ModClassicaActivity
import com.example.opentrivia.utils.GiocoUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class SfidaFragment : Fragment() {
    private lateinit var sfidaContainer: LinearLayout
    private lateinit var database: FirebaseDatabase
    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance()
         uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       val view = inflater.inflate(R.layout.menu_sfida, container, false)

        sfidaContainer = view.findViewById(R.id.linearLayout)


       val uid: String = FirebaseAuth.getInstance().currentUser?.uid.toString()

        val sfideRef =
            database.getReference("users").child(uid).child("sfide")


        sfideRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(sfideSnapshot: DataSnapshot) {

                for (sfide in sfideSnapshot.children){

                    val sfideIterator = sfideSnapshot.children.iterator()
                    // Avvia il processo delle partite in corso
                    processaSfide(sfideIterator) {
                        // Questa callback verrà chiamata quando tutte le partite sono state processate
                        // Puoi fare qui qualsiasi cosa vuoi fare dopo aver completato il ciclo
                    }

                }


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        return view
    }

    fun processaSfide(sfide: Iterator<DataSnapshot>, callback: () -> Unit) {
        if (!sfide.hasNext()) {
            // Quando tutti gli elementi sono stati processati, chiamare la callback
            callback()
            return
        }
        val nomeMio = FirebaseAuth.getInstance().currentUser?.displayName.toString()
        val modalitaRef= database.getReference("partite").child("classica")



        val sfida = sfide.next()


        val inflater = LayoutInflater.from(requireContext())

        var sfida1 = sfida.key.toString()
        val gameView = inflater.inflate(R.layout.sfida_item_layout, sfidaContainer, false)

        val opponentNameTextView = gameView.findViewById<TextView>(R.id.opponentNameTextView)
        val modalita = gameView.findViewById<TextView>(R.id.modalita)
        val accetta = gameView.findViewById<Button>(R.id.Accetta)
        val rifiuta = gameView.findViewById<Button>(R.id.Rifiuta)


        val partita = sfida.key.toString()
        val nomeModalita = sfida.child("modalita").value.toString()
        val difficolta = sfida.child("difficolta").value.toString()
        val avvID = sfida.child("avversarioID").value.toString()
        val avvNome = sfida.child("avversario").value.toString()
        var topic = "nessuno"
        if (sfida.hasChild("topic")){
             topic = sfida.child("topic").value.toString()
        }

        opponentNameTextView.text = avvNome
        modalita.text = nomeModalita

        accetta.setOnClickListener{
            val sfidaRef =  database.getReference("users").child(uid).child("sfide").child(partita)

            sfidaRef.removeValue()

            //CONTROLLA MODALITA E AVVIA INTENT ACTIVITY MODALITA DI GIOCO CONNETTENDOTI ALLA PARTITA CON ID CORRISPONDENTE
            lateinit var intent: Intent
            when (nomeModalita) {
                "classica" -> {intent = Intent(activity, ModClassicaActivity::class.java)}
                "argomento singolo" -> {intent = Intent(activity, ModArgomentoActivity::class.java)}
                "a tempo" -> {intent = Intent(activity, ModATempoActivity::class.java)}
            }
            intent.putExtra("difficolta", difficolta)
            intent.putExtra("avversario", avvID)
            intent.putExtra("avversarioNome", avvNome)
            intent.putExtra("sfidaAccettata", true)
            intent.putExtra("partita", partita)
            intent.putExtra("topic", topic)
            sfidaContainer.removeView(gameView)
            startActivity(intent)
        }

        rifiuta.setOnClickListener {
            //CANCELLA IL NODO NEL DATABASE DELLA PARTITA
            val refToRemove =  database.getReference("partite").child(nomeModalita).child(difficolta).child(partita)
            refToRemove.removeValue()
            val sfidaRef =  database.getReference("users").child(uid).child("sfide").child(partita)
            sfidaRef.removeValue()
            sfidaContainer.removeView(gameView)
        }

        if (nomeModalita == "a tempo" || nomeModalita == "argomento singolo") {
            if (sfida.child("fineTurno").value.toString() == "si") {
                sfidaContainer.addView(gameView)
            }
        }
        else {
            sfidaContainer.addView(gameView)
        }



    }
}