package com.example.opentrivia.gioco.classica

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
import android.graphics.PathMeasure
import android.graphics.Typeface
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.opentrivia.R
import com.example.opentrivia.utils.DatabaseUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlin.collections.ArrayList
import com.example.opentrivia.utils.ModClassicaUtils

class RuotaFragment : Fragment() {

    private lateinit var wheelView: View
    private lateinit var ruotaButton: Button
    private lateinit var imageView3: ImageView
    private lateinit var viewsArgMiei: Array<View>
    private lateinit var viewsArgAvv: Array<View>

    private lateinit var rettangolo1:View
    private lateinit var rettangolo2:View
    private lateinit var rettangolo3:View
    private lateinit var user:TextView
    private lateinit var avversario:TextView


    private var selectedTopic: String? = null
    private var isWheelStopped: Boolean = true
    lateinit var topic: String

    private lateinit var database: FirebaseDatabase
    private lateinit var modClassicaActivity: ModClassicaActivity
    private var diameter = 0
    private var viewAdattata= false

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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.mod_classica_ruota, container, false)
        wheelView = view.findViewById(R.id.wheelView)
        ruotaButton = view.findViewById(R.id.ruotaButton)
        imageView3 = view.findViewById(R.id.imageView3)

        viewsArgMiei = arrayOf(
            view.findViewById(R.id.storia),
            view.findViewById(R.id.sport),
            view.findViewById(R.id.scienze),
            view.findViewById(R.id.arte),
            view.findViewById(R.id.geografia),
            view.findViewById(R.id.culturaPop)
        )

        viewsArgAvv = arrayOf(
            view.findViewById(R.id.storia2),
            view.findViewById(R.id.sport2),
            view.findViewById(R.id.scienze2),
            view.findViewById(R.id.arte2),
            view.findViewById(R.id.geografia2),
            view.findViewById(R.id.culturaPop2)
        )

        rettangolo1 = view.findViewById(R.id.rettangolo1)
        rettangolo2 = view.findViewById(R.id.rettangolo2)
        rettangolo3 = view.findViewById(R.id.rettangolo3)
        user=view.findViewById(R.id.user)
        avversario=view.findViewById(R.id.avversario)


        adattaSchermo()



//PRENDIAMO DATI DELLA PARTITA DALL ACTIVITY
        modClassicaActivity = activity as ModClassicaActivity
        val partita = modClassicaActivity.partita
        val modalita = "classica"
        val difficolta = modClassicaActivity.difficolta

        //PRENDIAMO I RIFERIMENTI DELLA PARTITA SUL DATABASE
        database = FirebaseDatabase.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val giocatoreRef= database.getReference("partite").child(modalita).child(difficolta).child(partita).child("giocatori").child(uid)
        val giocatoriRef= database.getReference("partite").child(modalita).child(difficolta).child(partita).child("giocatori")


        //SCRIVIAMO NOME UTENTE E NOME AVVERSARIO
        user.text= FirebaseAuth.getInstance().currentUser?.displayName.toString()
        DatabaseUtils.getAvversario(modalita, difficolta, partita){ _, _, nomeAvv ->
            this.avversario.text = nomeAvv
        }


        //PRENDIAMO DAL DATABASE GLI ARGOMENTI CONQUISTATI PER POI COLORARLI
        ModClassicaUtils.getArgomentiConquistati(giocatoriRef) { argomentiMiei, argomentiAvversario ->
            coloraQuadratini(argomentiMiei,true)
            coloraQuadratini(argomentiAvversario, false)
        }

        // LEGGIAMO LE RISPOSTE CORRETTE DI FILA PER COLORARE I 3 RETTANGOLI IN BASSO
        ModClassicaUtils.leggiRisposteCorrette(giocatoreRef) {statoRiposte ->
            coloraStatoRisposte(statoRiposte)
        }


        //LISTENER BOTTONE PER GIRARE LA RUOTA
        ruotaButton.setOnClickListener {
            if (isWheelStopped) {
                // SCEGLIAMO UN ARGOMENTO IN MODO RANDOM
                val randomTopic = getRandomTopic()
                // GIRIAMO LA RUOTA IN BASE ALL ARGOMENTO USCITO
                rotateWheel(randomTopic)
            }


        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


       // PRENDIAMO DIAMETRO E CHIAMAMO FUNZIONE PER DISEGNARE RUOTA
       // Senza il metodo wheelView.post, potrebbe succedere che il width del wheelView non sia ancora stato determinato
        wheelView.post {
            diameter = wheelView.width
            drawWheelOverlay()
        }
    }


    // funzione che genera un argomento random (indice random)
    private fun getRandomTopic(): String {
        val randomIndex = Random().nextInt(topics.size)
        return topics[randomIndex]
    }


    //funzione che fa ruotare la ruota
    private fun rotateWheel(topic: String) {

        //angolo iniziale
        val fromAngle = currentAngle

        //10 giri più l'angolo del topic
        val toAngle = 3600f + calculateTargetAngle(topic)

        //istanziamo l'animazione della rotazione
        val rotateAnimation = RotateAnimation(
            fromAngle, toAngle,
            //0,5 perchè il cerchio deve ruotare rispetto al suo centro
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )

        rotateAnimation.duration = 3000

        //se la trasformazione dovuta alla rotazione deve persistere dopo l'animazione
        rotateAnimation.fillAfter = true

        //costruttore listener dell'animazione
        rotateAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                isWheelStopped = false
            }

            override fun onAnimationEnd(animation: Animation?) {
                //perchè deve ruotare solo una volta se si preme il tasto più volte
                isWheelStopped = true
                // perchè in toAngle abbiamo 3600 + angolo corrente
                //esempio: 3680/360= 10, resto 80 gradi
                currentAngle = toAngle % 360f


                selectedTopic = topic
                showToast("Selected topic: $topic")

                //notifichiamo l'activity con il topic selezionato
                val activity = requireActivity() as ModClassicaActivity
                activity.passTopicToActivity(topic)
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }
        })

        //facciamo partire l'animazione
        wheelView.startAnimation(rotateAnimation)
    }


    private fun calculateTargetAngle(topic: String): Float {
        val angle: Float
        val topicCount = topics.size

        // sezione angolare per ogni topic
        val angoloSezione = 360f / topicCount

        val index = topics.indexOf(topic)

        // calcolo la posizione dell'inizio della sezione angolare
        //1° topic 0 gradi(index=0), 2° 51,4 gradi..
        val startAngle = index * angoloSezione

        //es: argomento a 90 gradi (a destra del cerchio)
        //se ruoto di 90, l'animazione lo fa in senso orario,
        //quindi in alto mi trovo l'argomento a sinistra del cerchio.
        //voglio invece che ruota di 270 gradi.
        angle = 360f - startAngle

        return angle
    }


    private fun drawWheelOverlay() {


        //Ci serve una Bitmap nella quale disegneremo il cerchio e la setteremo come sfondo della wheelview.
        //Una Bitmap è una rappresentazione digitale di un'immagine in cui ogni pixel dell'immagine
        //è rappresentato da un valore numerico.
        //In pratica, una bitmap è una griglia di pixel
        val overlayBitmap = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888)


        //rappresenta la superficie su cui verranno disegnati gli elementi grafici (sezioni per ogni topic)
        val canvas = Canvas(overlayBitmap)

        //PER AVERE COORDINATE DEL CENTRO DEL CERCHIO DA DISEGNARE
        val center = diameter / 2f

        //creo un Percorso
        val path = Path()
        // creo un cerchio
        path.addCircle(center, center, diameter / 2f, Path.Direction.CW)


        //per le scritte
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        //ADATTIAMO DIOMENSIONE SCRITTA SE LA RUOTA è STATA ADATTATA DA ADATTASCHERMO()
        if (!viewAdattata) {
            textPaint.textSize = 60f
        }
        else {
            textPaint.textSize = 40f
        }
        textPaint.color = Color.BLACK
        val boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD)
        textPaint.typeface = boldTypeface


        //to float per le operazioni  più avanti
        val numeroTopic = topics.size.toFloat()

        // angolo del cerchio da dedicare per ogni topic
        val angoloSezione = 360f / numeroTopic


        //mi calcolo il perimetro del cerchio
        val pathMeasure = PathMeasure(path, false)
        val perimetro = pathMeasure.length

        //lunghezza del perimetro del cerchio da dedicare per ogni topic
        val arco = perimetro/numeroTopic


        //per ogni materia, la scrivo in una certa posizione e una certa angolazione nel cerchio
        for (i in topics.indices) {
            //inizio della sezione angolare
            val inizioAngoloSezione = i * angoloSezione

            val text = topics[i]

            //ci serve per centrare la scritta nella sua sezione
            val textWidth = textPaint.measureText(text)


            //scelgo il colore della sezione
            val backgroundPaint = Paint()
            backgroundPaint.color = colors[i]

            // Disegna la sezione
            canvas.drawArc(
                0f,
                0f,
                diameter.toFloat(),
                diameter.toFloat(),
                inizioAngoloSezione,
                angoloSezione,
                true,
                backgroundPaint
            )

            //SE JOLLY VOGLIO IL TESTO IN GIALLO (è l'ultimo quindi non ci serve rimettere il colore a nero)
            if (text == "jolly") {
                textPaint.color = Color.YELLOW
            }

            //HOFFSET PER DIRE A CHE LUNGHEZZA DEL PERIMETRO SCRIVERE (0 SAREBBE A DESTRA DEL CERCHIO)
            //VOFFSET PER DIRE A CHE DISTANZA DAL PERIMETRO SCRIVERE
            canvas.drawTextOnPath(
                text,
                path,
                (i.toFloat()/numeroTopic) * perimetro + arco/ 2f - textWidth / 2f,
                60f,
                textPaint
            )
        }

        //setto la bitmap come sfondo di wheelview
        wheelView.background = BitmapDrawable(resources, overlayBitmap)
        //270 perchè voglio il primo argomento in alto, -angolosezione/2 perchè lo voglio centrato
        wheelView.rotation = 270f - angoloSezione/2f
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }




    private fun coloraQuadratini(argomenti: ArrayList<String>, miei: Boolean) {

        //se sono i miei creo la lista con le mie view, altrimenti con quelle dell'avversario
        val quadratini = if (miei) viewsArgMiei
        else viewsArgAvv

        for (argomento in argomenti){
            //storia e storia2 hanno stesso tag e cosi via,
            //quindi se in Argomenti c'è "storia",indexOfFirst() e first() mi ritorna
            //la view storia o storia2 in base alla lista quadratini.
            val indice = quadratini.indexOfFirst { it.tag == argomento }
            quadratini.first{ it.tag == argomento }.setBackgroundColor(colors[indice])
        }
    }

    private fun coloraStatoRisposte (stato: Int){
        if (stato >= 1)rettangolo1.setBackgroundColor(Color.parseColor("#FFEB3B"))
        if (stato >= 2)rettangolo2.setBackgroundColor(Color.parseColor("#FFEB3B"))
        if (stato >= 3)rettangolo3.setBackgroundColor(Color.parseColor("#FFEB3B"))
    }



private fun adattaSchermo() {
    val widthPixel = Resources.getSystem().displayMetrics.widthPixels
    val density = Resources.getSystem().displayMetrics.xdpi

    val widthDp = (widthPixel * 160/density).toInt()

    if (widthDp <= 400){

        viewAdattata = true
        val layoutParams1 = wheelView.layoutParams as ConstraintLayout.LayoutParams
        diameter =  resources.getDimensionPixelSize(R.dimen.wheel_diameter)
        layoutParams1.width= diameter
        layoutParams1.height= diameter
        wheelView.layoutParams= layoutParams1

        val layoutParams2 = imageView3.layoutParams as ConstraintLayout.LayoutParams
        val width =  resources.getDimensionPixelSize(R.dimen.imageView3width)
        val height =  resources.getDimensionPixelSize(R.dimen.imageView3height)
        layoutParams2.height= height
        layoutParams2.width= width


    }
}


}

