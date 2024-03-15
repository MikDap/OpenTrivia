package com.example.opentrivia.gioco.argomento_singolo

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.opentrivia.menu.MenuActivity

import com.example.opentrivia.R
import com.example.opentrivia.utils.DatabaseUtils
import com.example.opentrivia.utils.GiocoUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



class SceltaMultiplaFragmentArgSingolo : Fragment() {

    private var database = FirebaseDatabase.getInstance()
    private lateinit var domanda: TextView
    private lateinit var risposta1: Button
    private lateinit var risposta2: Button
    private lateinit var risposta3: Button
    private lateinit var risposta4: Button
    private lateinit var modArgomentoActivity: ModArgomentoActivity

    private lateinit var rispostaCorretta: String


    private lateinit var partita: String
    private lateinit var modalita: String
    private lateinit var difficolta: String
    private lateinit var topic: String

    var rispostaData = false

    private val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private var ritirato = false
    private var avvRitirato = false

    lateinit var giocatoriRef: DatabaseReference
    lateinit var ritiratoRef: DatabaseReference
    lateinit var risposteRef: DatabaseReference
    var tempoPerApiTrascorso = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =
            inflater.inflate(R.layout.mod_argomento_singolo_scelta_multipla, container, false)
       modArgomentoActivity = activity as ModArgomentoActivity
        giocatoriRef = database.getReference("partite").child(modalita).child(difficolta).child(partita).child("giocatori")
        ritiratoRef = giocatoriRef.child(uid)
        risposteRef = giocatoriRef.child(uid).child(topic)

     onBackPressed()

        domanda = view.findViewById(R.id.domanda_arg)
        risposta1 = view.findViewById(R.id.risposta1_arg)
        risposta2 = view.findViewById(R.id.risposta2_arg)
        risposta3 = view.findViewById(R.id.risposta3_arg)
        risposta4 = view.findViewById(R.id.risposta4_arg)


        domanda.text = modArgomentoActivity.domanda
        risposta1.text = modArgomentoActivity.listaRisposte[0]
        risposta2.text = modArgomentoActivity.listaRisposte[1]
        risposta3.text = modArgomentoActivity.listaRisposte[2]
        risposta4.text = modArgomentoActivity.listaRisposte[3]
        rispostaCorretta = modArgomentoActivity.rispostaCorretta

        Handler(Looper.getMainLooper()).postDelayed({
            tempoPerApiTrascorso = true
        }, 5000)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

             controllaRitiro()

        val listaRisposte = listOf(risposta1, risposta2, risposta3, risposta4)

        risposta1.setOnClickListener {
          controllaRisposta(risposta1)
            GiocoUtils.evidenziaRispostaCorretta(listaRisposte, rispostaCorretta)
        }

        risposta2.setOnClickListener {
            controllaRisposta(risposta2)
            GiocoUtils.evidenziaRispostaCorretta(listaRisposte, rispostaCorretta)
        }

        risposta3.setOnClickListener {
            controllaRisposta(risposta3)
            GiocoUtils.evidenziaRispostaCorretta(listaRisposte, rispostaCorretta)
        }

        risposta4.setOnClickListener {
            controllaRisposta(risposta4)
            GiocoUtils.evidenziaRispostaCorretta(listaRisposte, rispostaCorretta)
        }

    }








    fun setParametriPartita(
        partita: String,
        modalita: String,
        difficolta: String,
        topic: String
    ) {
        this.partita = partita
        this.modalita = modalita
        this.difficolta = difficolta
        this.topic = topic
    }


    fun finePartita() {

        if(modArgomentoActivity.avversario != "casuale" && modArgomentoActivity.sfidaAccettata == "false"){
            val sfidaRef = database.getReference("users").child(modArgomentoActivity.avversario).child("sfide").child(partita)
            sfidaRef.child("fineTurno").setValue("si")
        }

        DatabaseUtils.getAvversario(modalita, difficolta, partita){ giocatore2esiste, avversario, nomeAvv ->
            DatabaseUtils.getRispCorrette(modalita, difficolta, partita){ risposte1, risposte2 ->
                if (ritirato) {
                    giocatoriRef.child(uid).child("fineTurno").setValue("si")
                    //SE ANCORA NON SI CONNETTE NESSUNO CANCELLO LA PARTITA
                    if(!giocatore2esiste) {
                        DatabaseUtils.spostaInPartiteTerminate(partita, modalita, difficolta, uid, risposte1, risposte2)
                    }
                }
                else if (avvRitirato){
                    giocatoriRef.child(uid).child("fineTurno").setValue("si")

                    DatabaseUtils.spostaInPartiteTerminate(partita, modalita, difficolta, uid, risposte1, risposte2)
                    DatabaseUtils.spostaInPartiteTerminate(partita, modalita, difficolta, avversario, risposte1, risposte2)

                    GiocoUtils.schermataVittoria(requireActivity().supportFragmentManager, R.id.fragmentContainerViewGioco2, nomeAvv, risposte1, risposte2, "argomento singolo")
                           }
                else {
                    if (!giocatore2esiste) {
                        giocatoriRef.child(uid).child("fineTurno").setValue("si")
                        GiocoUtils.schermataAttendi(requireActivity().supportFragmentManager, R.id.fragmentContainerViewGioco2)
                    } else {
                        giocatoriRef.child(uid).child("fineTurno").setValue("si")
                        DatabaseUtils.spostaInPartiteTerminate(partita, modalita, difficolta, uid, risposte1, risposte2)
                        DatabaseUtils.spostaInPartiteTerminate(partita, modalita, difficolta, avversario, risposte1, risposte2)

                        when {
                            risposte1 > risposte2 -> GiocoUtils.schermataVittoria(requireActivity().supportFragmentManager, R.id.fragmentContainerViewGioco2, nomeAvv, risposte1, risposte2, "argomento singolo")
                            risposte1 == risposte2 -> GiocoUtils.schermataPareggio(requireActivity().supportFragmentManager, R.id.fragmentContainerViewGioco2, nomeAvv, risposte1, risposte2, "argomento singolo")
                            else -> GiocoUtils.schermataSconfitta(requireActivity().supportFragmentManager, R.id.fragmentContainerViewGioco2, nomeAvv, risposte1, risposte2, "argomento singolo")
                        }
                    }
                }
            }
        }
    }

    fun controllaRitiro() {
        giocatoriRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (giocatore in dataSnapshot.children) {

                    var giocatore1 = giocatore.key.toString()
                    if (giocatore.hasChild("ritirato")) {
                        if (giocatore1 != uid){
                            avvRitirato = true
                            finePartita()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }



    fun controllaRisposta(risposta: Button) {
        if (!rispostaData) {
            lifecycleScope.launch {
                if (GiocoUtils.QuestaèLaRispostaCorretta(risposta, rispostaCorretta)) {
                    DatabaseUtils.updateRisposte(risposteRef, "risposteCorrette")
                    DatabaseUtils.updateStatTopic(topic, "corretta")
                } else {
                    DatabaseUtils.updateRisposte(risposteRef, "risposteSbagliate")
                    DatabaseUtils.updateStatTopic(topic, "sbagliata")
                }

                //Controlliamo le risposte totali, se 10 finisce la partita senno prossima domanda
                modArgomentoActivity.contatoreRisposte++
                if (modArgomentoActivity.contatoreRisposte < 10) {
                    while (!tempoPerApiTrascorso) {
                        delay(1000) // Aspetta un secondo prima di controllare nuovamente
                    }
                    modArgomentoActivity.getTriviaQuestion()
                } else {
                    finePartita()
                }
                rispostaData = true
            }
        }
    }

    fun onBackPressed(){
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Vuoi ritornare al menù?")
                alertDialog.setMessage("ATTENZIONE: uscendo perderai la partita")

                alertDialog.setPositiveButton("SI") { dialog: DialogInterface, which: Int ->

                    ritiratoRef.child("ritirato").setValue("si")
                    ritirato = true
                    finePartita()
                    val intent = Intent(requireContext(), MenuActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }

                alertDialog.setNegativeButton("NO") { dialog: DialogInterface, which: Int ->
                    dialog.dismiss()
                }

                alertDialog.show()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,callback)
    }
}