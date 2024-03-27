package com.example.opentrivia.menu

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.Navigation
import com.example.opentrivia.R
import com.example.opentrivia.gioco.classica.ModClassicaActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.system.exitProcess
import com.bumptech.glide.Glide


class Menu : Fragment() {
    private lateinit var startButton: Button
    private lateinit var partitaContainer: LinearLayout
    private lateinit var database: FirebaseDatabase
    private lateinit var giocaincorso: Button
    private lateinit var inattesa: Button
    private lateinit var background_game_item: ConstraintLayout
    private lateinit var visualizzaCronologia: Button
    private lateinit var notification: TextView
    private lateinit var sfida: ImageView
    private lateinit var gifImageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.menu, container, false)
        startButton = view.findViewById(R.id.startButton)
        visualizzaCronologia = view.findViewById(R.id.historyTextView)
        notification = view.findViewById(R.id.notificationBadge)
        partitaContainer = view.findViewById(R.id.linearLayout)
        sfida = view.findViewById(R.id.sfida)
        gifImageView = view.findViewById(R.id.gifImageView)

        adattaSchermo()

        numeroPartiteNonViste { contatore ->
            if (contatore == 0) {
                notification.visibility = View.INVISIBLE
            }
            notification.text = contatore.toString()
        }


        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Uscita Applicazione")
                alertDialog.setMessage("Vuoi uscire dall'applicazione?")

                alertDialog.setPositiveButton("SI") { dialog: DialogInterface, which: Int ->
                    finishAffinity(requireActivity())
                    exitProcess(0)
                }

                alertDialog.setNegativeButton("NO") { dialog: DialogInterface, which: Int ->
                    dialog.dismiss()
                }

                alertDialog.show()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        database = FirebaseDatabase.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val partiteInCorsoRef = database.getReference("users").child(uid).child("partite in corso")

        partiteInCorsoRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(partiteInCorso: DataSnapshot) {

                if (partiteInCorso.hasChildren()) {
                    val partiteIterator = partiteInCorso.children.iterator()
                    processaPartiteInCorso(partiteIterator) {
                    }
                }


            }


            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })





        startButton.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_menu_to_modalita)
        }
        visualizzaCronologia.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_menu_to_cronologiaPartite)
        }
        sfida.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_menu_to_sfidaFragment)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Carica la GIF
        Glide.with(this)
            .asGif()
            .load(R.drawable.giphy)
            .into(gifImageView)


        val activity = requireActivity() as MenuActivity


        if (activity.setteSecondi == true){
            gifImageView.visibility = View.VISIBLE
        }
        activity.registerListener(object : MenuActivity.VariableChangeListener {
            override fun onVariableChanged(newValue: Any) {
                // Implementazione dell'azione quando la variabile cambia
                if (newValue == true){
                    gifImageView.visibility = View.VISIBLE
                }
                if (newValue == false){
                    gifImageView.visibility = View.INVISIBLE
                }

            }
        })
    }
    fun leggiTurno(
        partita: String,
        difficolta: String,
        modalitaRef: DatabaseReference,
        callback: (turno: String) -> Unit
    ) {
        var turno = ""
        modalitaRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(modalitaRef: DataSnapshot) {


                if (modalitaRef.child(difficolta).child(partita).hasChild("Turno")) {
                    turno =
                        modalitaRef.child(difficolta).child(partita).child("Turno").value.toString()
                }

                callback(turno);
            }


            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    fun processaPartiteInCorso(partite: Iterator<DataSnapshot>, callback: () -> Unit) {
        if (!partite.hasNext()) {
            // Quando tutti gli elementi sono stati processati, chiamare la callback
            callback()
            return
        }
        val nomeMio = FirebaseAuth.getInstance().currentUser?.displayName.toString()
        val modalitaRef = database.getReference("partite").child("classica")


        val partita = partite.next()


        val inflater = LayoutInflater.from(requireContext())
        Log.d("1", "1")
        var partita1 = partita.key.toString()
        val gameView = inflater.inflate(R.layout.game_item_layout, partitaContainer, false)
        giocaincorso = gameView.findViewById(R.id.giocaincorso)
        background_game_item = gameView.findViewById(R.id.background_game_item)
        val opponentNameTextView = gameView.findViewById<TextView>(R.id.opponentNameTextView)
        val scoremeTextView = gameView.findViewById<TextView>(R.id.scoreme)
        val scoreavversarioTextView = gameView.findViewById<TextView>(R.id.scoreavversario)
        inattesa = gameView.findViewById(R.id.inattesa)
        val dashTextView = gameView.findViewById<TextView>(R.id.dashTextView)

        val heightPixel = Resources.getSystem().displayMetrics.heightPixels
        val density = Resources.getSystem().displayMetrics.density

        val heightDp = (heightPixel / density).toInt()
        if (heightDp <= 480){
            adattaScrollView(background_game_item,opponentNameTextView, scoremeTextView, scoreavversarioTextView, dashTextView, giocaincorso, inattesa)

        }




        var avversario = partita.child("Avversario").value.toString()
        var punteggioMio = partita.child("PunteggioMio").value.toString()
        var punteggioAvversario = partita.child("PunteggioAvversario").value.toString()

        scoremeTextView.text = punteggioMio
        opponentNameTextView.text = avversario
        scoreavversarioTextView.text = punteggioAvversario
        var difficolta = partita.child("difficolta").value.toString()

        leggiTurno(partita1, difficolta, modalitaRef) { turno ->
            Log.d("3", "3")
            if (turno == nomeMio) {
                giocaincorso.visibility = View.VISIBLE
                inattesa.visibility = View.INVISIBLE

                giocaincorso.setOnClickListener {
                    var intent = Intent(activity, ModClassicaActivity::class.java)
                    intent.putExtra("partita", partita1)
                    intent.putExtra("difficolta", difficolta)
                    startActivity(intent)
                }

            } else {
                giocaincorso.visibility = View.INVISIBLE
                inattesa.visibility = View.VISIBLE
                val drawableId = R.drawable.game_item_attesa2_background
                val drawable = ResourcesCompat.getDrawable(resources, drawableId, null)
                background_game_item.background = drawable


            }
            partitaContainer.addView(gameView)


            // Richiama la funzione ricorsivamente per processare il prossimo elemento
            processaPartiteInCorso(partite, callback)
        }
    }


    fun numeroPartiteNonViste(callback: (contatore: Int) -> Unit) {

        val uid: String = FirebaseAuth.getInstance().currentUser?.uid.toString()
        var partiteTerminateRef = FirebaseDatabase.getInstance().getReference("users").child(uid)
            .child("partite terminate")

        var contatore = 0

        partiteTerminateRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(partite: DataSnapshot) {

                for (modalita in partite.children) {
                    for (difficolta in modalita.children) {
                        for (partita in difficolta.children) {

                            if (!partita.hasChild("vista")) {
                                contatore++
                            }
                        }
                    }
                }
                callback(contatore)
            }

            override fun onCancelled(error: DatabaseError) {
                // gestione errore
            }
        })

    }


    fun adattaSchermo() {
        val startbuttonheight = resources.getDimensionPixelSize(R.dimen.startbuttonheight)
        val crossed_sword = resources.getDimensionPixelSize(R.dimen.crossed_sword)

        val widthPixel = Resources.getSystem().displayMetrics.widthPixels
        val density = Resources.getSystem().displayMetrics.xdpi

        val widthDp = (widthPixel * 160/density).toInt()


        if (widthDp <= 400) {

            val layoutParams = startButton.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.height = startbuttonheight
            startButton.layoutParams = layoutParams

            val layoutParams1 = sfida.layoutParams as ConstraintLayout.LayoutParams
            layoutParams1.width = crossed_sword
            sfida.layoutParams = layoutParams1
        }
    }


    fun adattaScrollView(background_game_item: ConstraintLayout,opponentNameTextView: TextView, scoremeTextView: TextView, scoreavversarioTextView: TextView, dashTextView: TextView, giocaincorso: TextView, inattesa: TextView){

        val game_item = resources.getDimensionPixelSize(R.dimen.game_item)
        val text_size_nome = resources.getDimensionPixelSize(R.dimen.text_size_nome)
        val text_size_punteggio = resources.getDimensionPixelSize(R.dimen.text_size_punteggio)
        val text_size_trattino = resources.getDimensionPixelSize(R.dimen.text_size_trattino)
        val button_gioca_inattesa_height = resources.getDimensionPixelSize(R.dimen.button_gioca_inattesa_height)
        val button_gioca_inattesa_width = resources.getDimensionPixelSize(R.dimen.button_gioca_inattesa_width)


        val layoutParams = background_game_item.layoutParams

        layoutParams.width= game_item
        layoutParams.height= game_item
        background_game_item.layoutParams= layoutParams

        opponentNameTextView.textSize = text_size_nome.toFloat()

        scoremeTextView.textSize = text_size_punteggio.toFloat()
        scoreavversarioTextView.textSize = text_size_punteggio.toFloat()

        dashTextView.textSize = text_size_trattino.toFloat()

        val layoutParams1 = giocaincorso.layoutParams as ConstraintLayout.LayoutParams
        layoutParams1.width = button_gioca_inattesa_width
        layoutParams1.height = button_gioca_inattesa_height
        giocaincorso.layoutParams = layoutParams1
        inattesa.layoutParams = layoutParams1
        giocaincorso.textSize = text_size_nome.toFloat()
        inattesa.textSize = text_size_nome.toFloat()
    }
}
