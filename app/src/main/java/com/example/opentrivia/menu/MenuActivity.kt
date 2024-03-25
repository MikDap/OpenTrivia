package com.example.opentrivia.menu

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import com.example.opentrivia.MainActivity
import com.example.opentrivia.R
import com.example.opentrivia.chat.ChatActivity
import com.example.opentrivia.listaSeguiti.ListaSeguitiActivity
import com.example.opentrivia.statistiche.StatisticheActivity
import com.google.firebase.auth.FirebaseAuth
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper

// L'activity eredita dalla classe AppCompatActivity, che fornisce funzionalità aggiuntive rispetto all'Activity standard.
class MenuActivity : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer
    var setteSecondi = false
    private val listeners = mutableListOf<VariableChangeListener>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        mediaPlayer = MediaPlayer.create(this, R.raw.menusong)

        addMenuProvider(object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {
                // Handle for example visibility of menu items
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_a_tendina, menu)
                

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                when (menuItem.itemId) {
                    R.id.chat -> {
                        // Qui puoi gestire l'evento di click sull'item "chat"
                        // Ad esempio, puoi navigare verso un altro fragment o eseguire altre azioni
                        // sostituisci "ChatFragment" con il nome del fragment desiderato
                        val intent = Intent(this@MenuActivity, ChatActivity::class.java)
                        startActivity(intent)
                        return true
                    }
                    R.id.statistiche -> {
                        val intent = Intent(this@MenuActivity, StatisticheActivity::class.java)
                        startActivity(intent)

                       return true
                    }
                    R.id.lista_seguiti -> {
                        val intent = Intent(this@MenuActivity, ListaSeguitiActivity::class.java)
                        startActivity(intent)
                    return true
                    }
                    R.id.logout -> {
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(this@MenuActivity, MainActivity::class.java)
                        startActivity(intent)
                        return true
                    }
                }
                return false
            }
        })


    }

   /* override fun onStop() {
        super.onStop()
        // Rilascia il MediaPlayer quando l'attività viene stoppata

        mediaPlayer.stop()
        mediaPlayer.release()
    }
*/
    fun stopMusic(){
       mediaPlayer.stop()
       mediaPlayer.release()
    }
    override fun onResume() {
        super.onResume()

        // Avvia la riproduzione solo se il MediaPlayer non è già in riproduzione
        if (!mediaPlayer.isPlaying) {
            setteSecondi = false
            notifyListeners(setteSecondi)

            mediaPlayer.setOnCompletionListener {
                // Quando la riproduzione della canzone è completata, riavviala


                setteSecondi = false
                notifyListeners(setteSecondi)
                mediaPlayer.seekTo(0)
                mediaPlayer.start()
                Handler(Looper.getMainLooper()).postDelayed({
                    setteSecondi = true
                    notifyListeners(setteSecondi)
                }, 7000)
            }

            mediaPlayer.start()

            Handler(Looper.getMainLooper()).postDelayed({
                setteSecondi = true
                notifyListeners(setteSecondi)
            }, 7000)
        }
    }

    interface VariableChangeListener {
        fun onVariableChanged(newValue: Any)
    }

    var myVariable: Any = ""
        set(value) {
            field = value
            notifyListeners(value)
        }

    private fun notifyListeners(newValue: Any) {
        listeners.forEach { it.onVariableChanged(newValue) }
    }

    fun registerListener(listener: VariableChangeListener) {
        listeners.add(listener)
    }

    fun unregisterListener(listener: VariableChangeListener) {
        listeners.remove(listener)
    }
}

