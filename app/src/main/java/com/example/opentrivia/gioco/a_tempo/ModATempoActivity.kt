package com.example.opentrivia.gioco.a_tempo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.opentrivia.R
import com.example.opentrivia.api.ChiamataApi
import com.example.opentrivia.utils.GiocoUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Random
class ModATempoActivity : AppCompatActivity(),ChiamataApi.TriviaQuestionCallback {
    private lateinit var partita: String
    private lateinit var difficolta: String
    private lateinit var chiamataApi: ChiamataApi
    private lateinit var database: FirebaseDatabase
    var domanda : String = ""
    var rispostaCorretta : String = ""
    var risposta1: String = ""
    var risposta2: String = ""
    var risposte = arrayOf(risposta1, risposta2)
    val topics =arrayOf("culturaPop","sport","storia","geografia","arte","scienze" )
    lateinit var topic: String
    lateinit var categoria: String
    private lateinit var inAttesa: String
    var timeStamp: Long = 0L
    var finalTime: Long = 60000L
    var fragment = "primo"

    lateinit var sfidaAccettata: String
    lateinit var avversario: String
    lateinit var avversarioNome: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mod_a_tempo_activity)
        difficolta = intent.getStringExtra("difficolta") ?: ""
        avversario = intent.getStringExtra("avversario").toString()
        avversarioNome = intent.getStringExtra("avversarioNome").toString()
        sfidaAccettata = intent.getBooleanExtra("sfidaAccettata", false).toString()

        partita = intent.getStringExtra("partita") ?: ""

        invalidateOptionsMenu()
    }
    fun startAtempo() {
        val partiteRef = database.getReference("partite").child("a tempo").child(difficolta)

        if (avversario == "casuale") {
            //SE POSSO ASSOCIO L'UTENTE A UNA PARTITA
            GiocoUtils.associaPartita("a tempo", difficolta, topic) { associato, partita ->
                if (associato) {
                    this.partita = partita
                }
                //ALTRIMENTI CREO UNA PARTITA
                else {
                    GiocoUtils.creaPartita("a tempo", partiteRef, topic) { partita ->
                        this.partita = partita
                    }
                }
            }
        }
        //HAI SFIDATO UN AMICO
        else {
            GiocoUtils.sfidaAmico("a tempo", difficolta, topic, avversario, avversarioNome)
        }
        //facciamo la chiamata api
        getTriviaQuestion()

    }
// quando viene scelto il topic sul fragment:

    //quando chiamataApi termina (callback)
    override fun onTriviaQuestionFetched(
        tipo:String,
        domanda: String,
        risposta_corretta: String,
        risposta_sbagliata_1: String,
        risposta_sbagliata_2:String,
        risposta_sbagliata_3:String
    ) {
        //salviamo la domanda e le risposte
        this.domanda = chiamataApi.domanda
        this.rispostaCorretta = chiamataApi.risposta_corretta
        val risposta_corretta = chiamataApi.risposta_corretta
        val risposta_sbagliata_1 = chiamataApi.risposta_sbagliata_1
// passiamo al secondo Fragment (DA GESTIRE IL PERMESSO DI RITORNARE INDIETRO DURANTE LA SCHERMATA DELLE DOMANDE E RISPOSTE)
        if (fragment == "primo"){
            timeStamp = System.currentTimeMillis()
            finalTime = timeStamp + 60000L
            fragment = "successivo"
        }
        // chiamiamo la funzione newInstance del fragment per passare il tempo rimanente al nuovo fragment
        val VFfragment = VeroFalsoFragment()
        VFfragment.partita = partita
        VFfragment.modalita = "a tempo"
        VFfragment.difficolta = difficolta
        VFfragment.topic = topic
        // getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, secondFragment).addToBackStack(null).commit();
        Handler(Looper.getMainLooper()).postDelayed({
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerViewGioco3, VFfragment).commit()
        }, 100)
    }


    //da commentare
    fun getTriviaQuestion() {
        topic=GiocoUtils.getRandomTopic(topics)
//chiamiamo la funzione per ottenere il numero delle categorie per il topic selezionato
        categoria = GiocoUtils.getCategoria(topic)
        chiamataApi = ChiamataApi("boolean",categoria,difficolta)
        chiamataApi.fetchTriviaQuestion(this)
        Log.d("getTriviaQuestion","siii")
    }

}