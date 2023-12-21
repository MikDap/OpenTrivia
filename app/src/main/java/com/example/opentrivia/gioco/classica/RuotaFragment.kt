package com.example.opentrivia.gioco.classica

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.graphics.Color
import android.view.animation.RotateAnimation
import android.view.animation.Animation
import android.widget.Button
import android.widget.Toast
import java.util.*
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.graphics.PathMeasure
import android.graphics.Typeface
import android.widget.TextView
import androidx.constraintlayout.widget.Guideline
import com.example.opentrivia.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.collections.ArrayList
import com.example.opentrivia.utils.ModClassicaUtils

class RuotaFragment : Fragment() {

    private lateinit var wheelView: View
    private lateinit var ruotaButton: Button


    private lateinit var prova: View
    private lateinit var storia : View
    private lateinit var sport : View
    private lateinit var scienze : View
    private lateinit var geografia : View
    private lateinit var arte : View
    private lateinit var culturaPop : View

    private lateinit var storia2 : View
    private lateinit var sport2 : View
    private lateinit var scienze2 : View
    private lateinit var geografia2 : View
    private lateinit var arte2 : View
    private lateinit var culturaPop2 : View

    private lateinit var rettangolo1:View
    private lateinit var rettangolo2:View
    private lateinit var rettangolo3:View
private lateinit var user:TextView
private lateinit var avversario:TextView

private lateinit var guideline1: Guideline
private lateinit var guideline2: Guideline

    private var selectedTopic: String? = null
    private var isWheelStopped: Boolean = true
    lateinit var topic: String

    private lateinit var database: FirebaseDatabase
    private lateinit var modClassicaActivity: ModClassicaActivity

    //da 0 perchè la ruota parte da 0 gradi
    private var currentAngle: Float = 0f
    private val topics =
        listOf("sport", "storia", "scienze", "arte", "geografia", "culturaPop", "jolly")
    private val colors = arrayOf(
        Color.parseColor("#FFEB3B"),
        Color.parseColor("#FFBB2F"),
        Color.parseColor("#4CAF50"),
        Color.parseColor("#FF0000"),
        Color.parseColor("#0000FF"),
        Color.parseColor("#FF00FF"),
        Color.parseColor("#BBBBBB")
    )

    //michele
    private var listener: MyFragmentListener? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.mod_classica_ruota, container, false)
        wheelView = view.findViewById(R.id.wheelView)
        ruotaButton = view.findViewById(R.id.ruotaButton)

        storia = view.findViewById(R.id.storia)
        sport = view.findViewById(R.id.sport)
        geografia = view.findViewById(R.id.geografia)
        arte = view.findViewById(R.id.arte)
        scienze = view.findViewById(R.id.scienze)
        culturaPop = view.findViewById(R.id.culturaPop)

        storia2 = view.findViewById(R.id.storia2)
        sport2 = view.findViewById(R.id.sport2)
        geografia2 = view.findViewById(R.id.geografia2)
        arte2 = view.findViewById(R.id.arte2)
        scienze2 = view.findViewById(R.id.scienze2)
        culturaPop2 = view.findViewById(R.id.culturaPop2)
        rettangolo1 = view.findViewById(R.id.rettangolo1)
        rettangolo2 = view.findViewById(R.id.rettangolo2)
        rettangolo3 = view.findViewById(R.id.rettangolo3)
        guideline1 = view.findViewById(R.id.guidelineVerticalStart1)

// Utilizza distanceInDp come riferimento per impostare le dimensioni delle viste nel tuo layout
        user=view.findViewById(R.id.user)
        avversario=view.findViewById(R.id.avversario)

        modClassicaActivity = activity as ModClassicaActivity
        val partita = modClassicaActivity.partita
        val modalita = "classica"
        val difficolta = modClassicaActivity.difficolta

        database = FirebaseDatabase.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val giocatoreRef= database.getReference("partite").child(modalita).child(difficolta).child(partita).child("giocatori").child(uid)
        val giocatoriRef= database.getReference("partite").child(modalita).child(difficolta).child(partita).child("giocatori")

        //nel caso un giocatore esce quando sta per passare alla schermata conquista         leggiRisposteDiFila(giocatoreRef)<<
        //mic 09
        user.text= FirebaseAuth.getInstance().currentUser?.displayName.toString()+" (me)"
        ModClassicaUtils.ottieniNomeAvversario(giocatoriRef) { nomeAvversario ->
            // Questo codice verrà eseguito quando la callback restituirà il nome dell'avversario
            avversario.text = nomeAvversario
        }
        //fine
        ModClassicaUtils.QualiArgomentiConquistati(giocatoriRef) { argomentiMiei, argomentiAvversario ->
            if (argomentiMiei.isEmpty())Log.d("argomentiMieiVuoti","si")
            if(argomentiMiei.isNotEmpty())  {      Log.d("argomentiMiei", argomentiMiei[0]) }
            coloraQuadratini(argomentiMiei,true)
            coloraQuadratini(argomentiAvversario, false)
        }

        ModClassicaUtils.leggiRisposteCorrette(giocatoreRef) {statoRiposte ->

            coloraStatoRisposte(statoRiposte)
        }
        ruotaButton.setOnClickListener {
            if (isWheelStopped) {
                // richiama la funzione per ottenere un argomento in modo random
                val randomTopic = getRandomTopic()
                // richiama la funzione per girare la ruota passandoci l'argomento che è uscito
                rotateWheel(randomTopic)
            }


        }








        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // ...




        wheelView.post {
            val backgroundColor = Color.parseColor("#FFC107") // Colore di sfondo desiderato
            drawWheelOverlay(backgroundColor)

        }
    }


    // funzione che genera un argomento random (indice random)
    private fun getRandomTopic(): String {
        val randomIndex = Random().nextInt(topics.size)
        return topics[randomIndex]
    }


    //funzione che fa ruotare la ruota
    private fun rotateWheel(topic: String) {

        val fromAngle = currentAngle
        Log.d("from", fromAngle.toString())

        val toAngle = 3600f + calculateTargetAngle(topic)
        Log.d("to", toAngle.toString())
        Log.d("calculate", calculateTargetAngle(topic).toString())
        val rotateAnimation = RotateAnimation(
            fromAngle, toAngle,
            //0,5 perchè il cerchio deve ruotare rispetto al suo centro
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )

        rotateAnimation.duration = 3000

        rotateAnimation.fillAfter = true

        rotateAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                isWheelStopped = false
            }

            override fun onAnimationEnd(animation: Animation?) {
                isWheelStopped = true
                // perchè in toAngle abbiamo 3600 + angolo corrente
                currentAngle = toAngle % 360f
                Log.d("currentAngle", currentAngle.toString())


                selectedTopic = topic
                showToast("Selected topic: $topic")

                passVariableToActivity(topic)
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }
        })
// fa iniziare l'animazione
        wheelView.startAnimation(rotateAnimation)
    }


    private fun calculateTargetAngle(topic: String): Float {
        var angle = 0f
        val topicCount = topics.size

        // sezione angolare per ogni topic
        val angoloSezione = 360f / topicCount

        val index = topics.indexOf(topic)

        // calcolo la posizione dell'inizio della sezione angolare
        val startAngle = index * angoloSezione

        //1° topic 0 gradi, 2° 51,4 gradi (startAngle)
        // ma valore positivo sposta in verso orario, quindi 360 - startAngle
        angle = 360f - startAngle

        return angle
    }


    private fun drawWheelOverlay(backgroundColor: Int) {

        val diameter = wheelView.width

        // ci serve una Bitmap per creare il cerchio
        val overlayBitmap = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888)
//server per disegnare
        val canvas = Canvas(overlayBitmap)

        val centerX = diameter / 2f
        val centerY = diameter / 2f


        val topicCount = topics.size

        // angolazione da dedicare per ogni topic
        val angoloSezione = 360f / topicCount

        //creo un Percorso
        val path = Path()
        // creo un cerchio
        path.addCircle(centerX, centerY, diameter / 2f, Path.Direction.CW)

        val backgroundPaint = Paint()
        backgroundPaint.color = backgroundColor

//disegno il cerchio con il percorso specificato e il colore specificato
        canvas.drawPath(path, backgroundPaint)

        //ci serve per le scritte
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        textPaint.textSize = 60f
        textPaint.color = Color.BLACK
        val boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD)
        textPaint.setTypeface(boldTypeface)


        //per ogni materia, la scrivo in una certa posizione e una certa angolazione nel cerchio
        for (i in topics.indices) {
            //inizio della sezione angolare
            val startAngle = i * angoloSezione
            //fine sezione angolare
            val endAngle = (i + 1) * angoloSezione

            val text = topics[i]
            Log.d("materia", text)

            val textWidth = textPaint.measureText(text)
//centro della sezione angolare relativa all'argomento
            val textAngle = (startAngle + endAngle) / 2f

            Log.d("textAngle", textAngle.toString())

// creo un altro oggetto canvas passandogli la stessa Bitmap, perchè se usassi quella fuori dal ciclo
// quando uso il metodo rotate per argomenti successivi al primo mi va a ruotare di nuovo gli argomenti
            //precedenti a quello del ciclo in corso, i quali erano nell'angolazione giusta
            val canvas2 = Canvas(overlayBitmap)
            // 0.. 51,4.. 102,8.....308,6 senso orario
            canvas2.rotate(startAngle, centerX, centerY)

//scelgo l'indice del colore dello spicchio
            val colorIndex = i % colors.size
            val backgroundPaint = Paint()
            backgroundPaint.color = colors[colorIndex]

            // Disegna lo spicchio
            // (startAngle + 270f perchè nel drawTextOnPath viene modificata
            // di 270 gradi la posizione della scritta nell'hOffset,
            // - (angoloSezione/2f) per centrare la sezione)
            canvas.drawArc(
                0f,
                0f,
                diameter.toFloat(),
                diameter.toFloat(),
                startAngle + 270f - (angoloSezione / 2f),
                angoloSezione,
                true,
                backgroundPaint
            )


//mi calcolo il perimetro del cerchio
            val pathMeasure = PathMeasure(path, false)
            val pathLength = pathMeasure.length

            if (text == "jolly") {
                textPaint.color = Color.YELLOW
            }
            //dovuto scrivere cosi perchè hOffset non può uscire fuori dalla lunghezza del percorso ( deve essere tra 0 e perimetro del cerchio)
            canvas2.drawTextOnPath(
                text,
                path,
                (3 * pathLength) / 4f - textWidth / 2f,
                60f,
                textPaint
            )
            Log.d("textwidth", textWidth.toString())
        }

        wheelView.background = BitmapDrawable(resources, overlayBitmap)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


    //Michele da 245 a 268
    //crea un listener (funge da contratto tra il Fragment e la Activity)
    interface MyFragmentListener {
        //implementato in ModClassicaActivity
        fun onVariablePassed(variable: String) {
        }
    }

    //estrae l'Activity ospitante utilizzando requireActivity() e controlla che
// l'Activity implementi l'interfaccia MyFragmentListener.
// Quindi, chiama il metodo onVariablePassed
    fun passVariableToActivity(variable: String) {
        val activity = requireActivity() as MyFragmentListener
        activity.onVariablePassed(variable)
    }

    //viene chiamato quando il Fragment viene associato all'Activity ospitante DA VEDERE SE LEVANDOLO, FUNZIONA
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MyFragmentListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement MyFragmentListener")
        }
    }


    fun leggiRisposteDiFila(
        giocatoreRef: DatabaseReference
    ) {
        giocatoreRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(giocatore: DataSnapshot) {


                if (giocatore.hasChild("risposteDiFila")) {

                    var risposte_di_fila =
                        giocatore.child("risposteDiFila").value.toString().toInt()

                    if (risposte_di_fila == 3) {

                        // chiama schermata conquista argomento
                        giocatoreRef.child("risposteDiFila").setValue(0)

                        modClassicaActivity.chiamaConquista()
                    }

                }


            }


            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }



    // METTERE SE ARGOMENTIMIEI O ARGOMENTIAVVERS
    fun coloraQuadratini(Argomenti: ArrayList<String>, miei: Boolean) {

        for (argomento in Argomenti) {

            if (miei) {
                when (argomento) {

                    "storia" -> {
                        storia.setBackgroundColor(Color.parseColor("#FFBB2F"))
                    }

                    "sport" -> {
                        sport.setBackgroundColor(Color.parseColor("#FFEB3B"))
                    }

                    "geografia" -> {
                        geografia.setBackgroundColor(Color.parseColor("#0000FF"))
                    }

                    "arte" -> {
                        arte.setBackgroundColor(Color.parseColor("#FF0000"))
                    }

                    "scienze" -> {
                        scienze.setBackgroundColor(Color.parseColor("#4CAF50"))
                    }

                    "culturaPop" -> {
                        culturaPop.setBackgroundColor(Color.parseColor("#FF00FF"))
                    }


                }

            }

            else {
                when (argomento) {

                    "storia" -> {
                        storia2.setBackgroundColor(Color.parseColor("#FFBB2F"))
                    }

                    "sport" -> {
                        sport2.setBackgroundColor(Color.parseColor("#FFEB3B"))
                    }

                    "geografia" -> {
                        geografia2.setBackgroundColor(Color.parseColor("#0000FF"))
                    }

                    "arte" -> {
                        arte2.setBackgroundColor(Color.parseColor("#FF0000"))
                    }

                    "scienze" -> {
                        scienze2.setBackgroundColor(Color.parseColor("#4CAF50"))
                    }

                    "culturaPop" -> {
                        culturaPop2.setBackgroundColor(Color.parseColor("#FF00FF"))
                    }


                }

            }
        }


    }

    fun coloraStatoRisposte (stato: Int){
        when(stato) {
            1 ->rettangolo1.setBackgroundColor(Color.parseColor("#FFEB3B"))

            2->{rettangolo1.setBackgroundColor(Color.parseColor("#FFEB3B"))
                rettangolo2.setBackgroundColor(Color.parseColor("#FFEB3B"))}

            3->{rettangolo1.setBackgroundColor(Color.parseColor("#FFEB3B"))
                rettangolo2.setBackgroundColor(Color.parseColor("#FFEB3B"))
                rettangolo3.setBackgroundColor(Color.parseColor("#FFEB3B")) }

        }
    }






}

