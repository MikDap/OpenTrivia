package com.example.opentrivia.gioco

import android.content.Context
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
import com.example.opentrivia.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class RuotaFragment : Fragment() {

    private lateinit var wheelView: View
    private lateinit var ruotaButton: Button
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
        Color.parseColor("#FFD1DC"),  // Rosa pastello
        Color.parseColor("#B2FFA5"),  // Verde pastello
        Color.parseColor("#AEC6FF"),  // Blu pastello
        Color.parseColor("#FFF8B3"),  // Giallo pastello
        Color.parseColor("#FFB2FF"),  // Magenta pastello
        Color.parseColor("#B3FFFF"),  // Ciano pastello
        Color.parseColor("#D3D3D3")   // Grigio pastello
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

        modClassicaActivity = activity as ModClassicaActivity
        val partita = modClassicaActivity.partita
        val modalita = "classica"
        val difficolta = modClassicaActivity.difficolta

        database = FirebaseDatabase.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val giocatoreRef = database.getReference("partite").child(modalita).child(difficolta).child(partita).child("giocatori").child(uid)


              leggiRisposteDiFila(giocatoreRef)



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
}

