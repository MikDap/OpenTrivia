package com.example.opentrivia

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.MenuProvider
import com.example.opentrivia.chat.ChatActivity
import com.example.opentrivia.listaAmici.ListaAmiciActivity
import com.example.opentrivia.statistiche.StatisticheActivity
import com.example.opentrivia.ui.theme.OpenTriviaTheme
import com.google.firebase.auth.FirebaseAuth


// L'activity eredita dalla classe AppCompatActivity, che fornisce funzionalità aggiuntive rispetto all'Activity standard.
class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)


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
                    R.id.lista_amici -> {
                        val intent = Intent(this@MenuActivity, ListaAmiciActivity::class.java)
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



}

