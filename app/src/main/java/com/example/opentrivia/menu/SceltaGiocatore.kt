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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SceltaGiocatore : Fragment() {

    private lateinit var database: FirebaseDatabase
    private lateinit var cercaPartitaButton: Button
    private lateinit var inAttesa: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val modalita = arguments?.getString("modalita")
        val difficolta = arguments?.getString("difficolta")



// prendiamo l'istanza del database (ci serve per creare sul database la partita)
        database = FirebaseDatabase.getInstance()
        // partiteRef = database/partite/modalita/difficolta
        val partiteRef = database.getReference("partite").child(modalita.toString()).child(difficolta.toString())
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_scelta_giocatore, container, false)
        cercaPartitaButton = view.findViewById(R.id.cercaPartitaButton)
        cercaPartitaButton.setOnClickListener {


            val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
            val name = FirebaseAuth.getInstance().currentUser?.displayName.toString()
            var condizioneSoddisfatta = false

            //Listener per database/partite/modalita/difficolta
            partiteRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    lateinit var intent: Intent
                    when (modalita) {
                        "classica" -> {intent = Intent(activity, ModClassicaActivity::class.java)}
                        "argomento singolo" -> {intent = Intent(activity, ModArgomentoActivity::class.java)}
                        "a tempo" -> {intent = Intent(activity, ModATempoActivity::class.java)}
                    }


/// Se database/partite/modalita/difficolta ha almeno una partita(con qualcuno in attesa):
                    if (dataSnapshot.hasChildren()) {
                    for (sottonodo in dataSnapshot.children) {
                        //se c'è almeno una partita con un giocatore in attesa..(lo associa)
                        if (sottonodo.child("inAttesa").value == "si") {
                            //prende id della partita
                            val partita = sottonodo.key.toString()
                            //setta database/partite/modalita/difficolta/giocatori/id
                            partiteRef.child(partita).child("giocatori").child(uid).setValue(name)
                            //cambia inAttesa in no
                            partiteRef.child(partita).child("inAttesa").setValue("no")
                            condizioneSoddisfatta = true

                            intent.putExtra("partita", partita)
                            intent.putExtra("difficolta",difficolta)
                            //avvia l'activity di gioco
                            startActivity(intent)

                            break
                        }
                    }



/// se database/partite/modalita/difficolta non ha partite:
                } else {
                    //crea id della partita ecc
                        val nuovaPartitaId = partiteRef.push().key.toString()
                        inAttesa = "si"
                        partiteRef.child(nuovaPartitaId).child("inAttesa")
                            .setValue(inAttesa)
                        partiteRef.child(nuovaPartitaId).child("giocatori")
                            .child(uid).setValue(name)
                        condizioneSoddisfatta = true
                        intent.putExtra("partita", nuovaPartitaId)
                        intent.putExtra("difficolta",difficolta)
                        startActivity(intent)

                    }





         //           Se database/partite/modalita/difficolta ha almeno una partita ma nessuno in Attesa
                if (condizioneSoddisfatta == false) {
                    val nuovaPartitaId = partiteRef.push().key.toString()
                    inAttesa = "si"
                    partiteRef.child(nuovaPartitaId).child("inAttesa")
                        .setValue(inAttesa)
                    partiteRef.child(nuovaPartitaId).child("giocatori")
                        .child(uid).setValue(name)
                 //   partiteRef.child(nuovaPartitaId).child("giocatori").child(uid).child(name).child("risposteCorrette").setValue(0)
                    condizioneSoddisfatta = true

                    intent.putExtra("partita", nuovaPartitaId)
                    intent.putExtra("difficolta",difficolta)
                    startActivity(intent)

                }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })





        }

        return view


}
}