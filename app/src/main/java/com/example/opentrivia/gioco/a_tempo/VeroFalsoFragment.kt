package com.example.opentrivia.gioco.a_tempo
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.opentrivia.MainActivity
import com.example.opentrivia.R
import com.example.opentrivia.utils.GiocoUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
class VeroFalsoFragment : Fragment() {
    private var database = FirebaseDatabase.getInstance()
    private lateinit var domanda: TextView
    private lateinit var risposta1: Button
    private lateinit var risposta2: Button
    //private lateinit var risposta3: Button
    //private lateinit var risposta4: Button
    private lateinit var modATempoActivity: ModATempoActivity
    private lateinit var rispostaCorretta: String
    lateinit var partita: String
    lateinit var modalita: String
    lateinit var difficolta: String
    lateinit var topic: String
    private  var contatoreRisposte = 0
    var timeStamp: Long = 0L
    var finalTime: Long = 60000L
    lateinit var progressBarView: TimeProgressBarView
    private var ritirato = false
    private var avvRitirato = false

    val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
    var giocatoriRef = database.getReference("partite").child(modalita).child(difficolta).child(partita).child("giocatori")
    var ritiratoRef = giocatoriRef.child(uid)
    var risposteRef = giocatoriRef.child(uid).child(topic)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        modATempoActivity= activity as ModATempoActivity
        timeStamp = modATempoActivity.timeStamp
        finalTime = modATempoActivity.finalTime
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_vero_falso, container, false)

        onBackPressed()

        domanda = view.findViewById(R.id.domandavf)
        risposta1 = view.findViewById(R.id.vfrisposta1)
        risposta2 = view.findViewById(R.id.vfrisposta2)
        domanda.text = modATempoActivity.domanda
        risposta1.text = "True"
        risposta2.text = "False"

        rispostaCorretta = modATempoActivity.rispostaCorretta
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val timeProgressBarView = view.findViewById<TimeProgressBarView>(R.id.timeProgressBar)
        timeProgressBarView.timeStamp = timeStamp
        timeProgressBarView.finalTime = finalTime
        timeProgressBarView.associatedFragment = this
        timeProgressBarView.startTimer()
        progressBarView = timeProgressBarView

        controllaRitiro()

        var rispostaData = false
        risposta1.setOnClickListener {
            if (!rispostaData) {
                if (risposta1.text == rispostaCorretta) {
                    risposta1.setBackgroundColor(Color.GREEN)
                    GiocoUtils.updateRisposte(risposteRef,"risposteCorrette")
                    GiocoUtils.updateStatTopic(topic,"corretta")
                } else {
                    risposta1.setBackgroundColor(Color.RED)
                    GiocoUtils.updateRisposte(risposteRef,"risposteSbagliate")
                    GiocoUtils.updateStatTopic(topic,"sbagliata")
                }
                rispostaData = true
                modATempoActivity.getTriviaQuestion()
            }
        }
        risposta2.setOnClickListener {
            if (!rispostaData) {
                if (risposta2.text == rispostaCorretta) {
                    risposta2.setBackgroundColor(Color.GREEN)
                    GiocoUtils.updateRisposte(risposteRef,"risposteCorrette")
                    GiocoUtils.updateStatTopic(topic,"corretta")
                } else {
                    risposta2.setBackgroundColor(Color.RED)
                    GiocoUtils.updateRisposte(risposteRef,"risposteSbagliate")
                    GiocoUtils.updateStatTopic(topic,"sbagliata")
                }
                rispostaData = true
                modATempoActivity.getTriviaQuestion()
            }
        }
    }


    fun finePartita() {
        var risposte1 = 0
        var risposte2 = 0
        var giocatore2esiste = false
        var avversario = ""
        var nomeAvv = ""

        giocatoriRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(listaGiocatori: DataSnapshot) {
                for (giocatore in listaGiocatori.children) {

                    if(giocatore.key.toString() != uid){
                        giocatore2esiste = true
                        avversario = giocatore.key.toString()
                        nomeAvv = giocatore.child("name").value.toString()
                    }


                    // A ENTRAMBI GIOCATORI, PER OGNI TOPIC PRENDO RISPOSTE CORRETTE
                    for (topic in giocatore.children) {

                        if (topic.hasChild("risposteTotali")) {

                            var giocatore1 = giocatore.key.toString()

                            // SE IO AGGIORNA MIE RISPOSTE
                            if (giocatore1 == uid) {
                                val risposteCorrette =
                                    topic.child("risposteCorrette").getValue(Int::class.java)
                                if (risposteCorrette != null) {
                                    risposte1 += risposteCorrette
                                }
                            }
                            //SE AVVERSARIO AGGIORNA SUE RISPOSTE
                            else {
                                val risposteCorrette =
                                    topic.child("risposteCorrette").getValue(Int::class.java)
                                if (risposteCorrette != null) {
                                    risposte2 += risposteCorrette
                                }

                            }
                        }
                    }


                }


                if (ritirato) {
                    giocatoriRef.child(uid).child("fineTurno").setValue("si")
                    //SE ANCORA NON SI CONNETTE NESSUNO CANCELLO LA PARTITA
                    if(!giocatore2esiste) {
                        GiocoUtils.spostaInPartiteTerminate(partita, modalita, difficolta, uid, risposte1, risposte2)
                    }
                }
                else if (avvRitirato){
                    giocatoriRef.child(uid).child("fineTurno").setValue("si")

                    GiocoUtils.spostaInPartiteTerminate(partita, modalita, difficolta, uid, risposte1, risposte2)
                    GiocoUtils.spostaInPartiteTerminate(partita, modalita, difficolta, avversario, risposte1, risposte2)

                    GiocoUtils.schermataVittoria(
                        requireActivity().supportFragmentManager,
                        R.id.fragmentContainerViewGioco2,
                        nomeAvv,
                        risposte1,
                        risposte2,
                        "argomento singolo"
                    )
                }
                else {
                    if (!giocatore2esiste) {
                        giocatoriRef.child(uid).child("fineTurno").setValue("si")
                        GiocoUtils.schermataAttendi(requireActivity().supportFragmentManager, R.id.fragmentContainerViewGioco2)
                    } else {
                        giocatoriRef.child(uid).child("fineTurno").setValue("si")
                        GiocoUtils.spostaInPartiteTerminate(partita, modalita, difficolta, uid, risposte1, risposte2)
                        GiocoUtils.spostaInPartiteTerminate(partita, modalita, difficolta, avversario, risposte1, risposte2)

                        when {
                            risposte1 > risposte2 -> GiocoUtils.schermataVittoria(requireActivity().supportFragmentManager, R.id.fragmentContainerViewGioco2, nomeAvv, risposte1, risposte2, "argomento singolo")
                            risposte1 == risposte2 -> GiocoUtils.schermataPareggio(requireActivity().supportFragmentManager, R.id.fragmentContainerViewGioco2, nomeAvv, risposte1, risposte2, "argomento singolo")
                            else -> GiocoUtils.schermataSconfitta(requireActivity().supportFragmentManager, R.id.fragmentContainerViewGioco2, nomeAvv, risposte1, risposte2, "argomento singolo")
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })


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
                    val intent = Intent(requireContext(), MainActivity::class.java)
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


    override fun onDestroyView() {
        super.onDestroyView()
        progressBarView.stopTimer()
    }
}
 