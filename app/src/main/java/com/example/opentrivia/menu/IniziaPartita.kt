package com.example.opentrivia.menu

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.opentrivia.R
import com.example.opentrivia.gioco.a_tempo.ModATempoActivity
import com.example.opentrivia.gioco.argomento_singolo.ModArgomentoActivity
import com.example.opentrivia.gioco.classica.ModClassicaActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class IniziaPartita : Fragment() {

    private lateinit var cercaPartitaButton: Button
    lateinit var selezione: String
    private lateinit var recyclerView: RecyclerView
    private lateinit var selezioneAvversario: TextView
    private val friendsList = mutableMapOf<String, String>()
    val adapter = IniziaPartitaAdapter(friendsList, this)
   lateinit var avversarioID: String
   lateinit var avversarioNome: String
    private val uid: String = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private val database = FirebaseDatabase.getInstance()
    private val amiciRef = database.getReference("users").child(uid).child("amici")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val modalita = arguments?.getString("modalita")
        val difficolta = arguments?.getString("difficolta")
        val selezione = arguments?.getString("selezione")
        if (selezione != null) {
            this.selezione = selezione
        }

             // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.menu_inizia_partita, container, false)
        cercaPartitaButton = view.findViewById(R.id.cercaPartitaButton)
        recyclerView = view.findViewById(R.id.recyclerViewFriends)
        selezioneAvversario = view.findViewById(R.id.selezionaAvversario)

        if (selezione == "casuale"){
            recyclerView.visibility = View.INVISIBLE
            selezioneAvversario.text = "Avversario: casuale"
        }


        val activity = requireActivity() as MenuActivity


        cercaPartitaButton.setOnClickListener {


                    lateinit var intent: Intent
                    when (modalita) {
                        "classica" -> {intent = Intent(activity, ModClassicaActivity::class.java)}
                        "argomento singolo" -> {intent = Intent(activity, ModArgomentoActivity::class.java)}
                        "a tempo" -> {intent = Intent(activity, ModATempoActivity::class.java)}
                    }

            if (selezione == "casuale") {
                intent.putExtra("difficolta", difficolta)
                intent.putExtra("avversario", "casuale")
                intent.putExtra("sfidaAccettata",false)
                intent.putExtra("avversarioNome", "/")
                startActivity(intent)
            }

            if (selezione == "amico") {
                if (!::avversarioID.isInitialized) {
                    Toast.makeText(requireContext(), "Seleziona un avversario!", Toast.LENGTH_SHORT).show()
                }
                else {
                    intent.putExtra("difficolta", difficolta)
                    intent.putExtra("avversario", avversarioID)
                    intent.putExtra("sfidaAccettata", false)
                    intent.putExtra("avversarioNome", avversarioNome)
                    startActivity(intent)
                }
            }


        }

        return view
}


fun nomeAvversario(nome:String){
    selezioneAvversario.text = "Avversario:" + nome
}


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        recyclerView.adapter = adapter


        // Aggiungi un ValueEventListener per ottenere i dati degli amici da Firebase
        amiciRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                friendsList.clear() // Svuota la lista prima di popolarla con i nuovi dati
                for (amicoSnapshot in snapshot.children) {
                    val amicoId = amicoSnapshot.key.toString()
                    val amico = amicoSnapshot.getValue(String::class.java)
                    if (amico != null) {
                        friendsList[amicoId] = (amico)
                    }
                }
                adapter.notifyDataSetChanged() // Aggiorna la RecyclerView quando i dati cambiano
            }

            override fun onCancelled(error: DatabaseError) {
                // Gestisci l'errore, se necessario
            }
        })
    }


}