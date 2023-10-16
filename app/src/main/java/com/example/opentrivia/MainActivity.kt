package com.example.opentrivia

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.MenuProvider
import com.example.opentrivia.ui.theme.OpenTriviaTheme
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.ktx.database
import androidx.navigation.Navigation.findNavController
import com.example.opentrivia.gioco.ModClassicaActivity
import com.example.opentrivia.listaAmici.ListaAmiciActivity
import com.example.opentrivia.listaAmici.ListaAmiciFragment

// L'activity eredita dalla classe AppCompatActivity, che fornisce funzionalità aggiuntive rispetto all'Activity standard.
class MainActivity : AppCompatActivity() {
    private lateinit var userMethods: UserMethods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

// Crea un ActivityResultLauncher che registra una callback
// per il contratto dei risultati dell'attività FirebaseUI:
        val user = Firebase.auth.currentUser
        //if (user == null) {

        val signInLauncher = registerForActivityResult(

            //classe fornita da FirebaseUI Auth che definisce un contratto
            // per avviare l'attività di autenticazione e gestire il risultato.
            FirebaseAuthUIActivityResultContract(),
            //Quando l'attività di autenticazione restituisce un risultato,
            // riceve il risultato res e lo passa al metodo onSignInResult
        ) { res ->
            this.onSignInResult(res)
        }


        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build(),
        )
        // Viene creato l'intento di autenticazione
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setIsSmartLockEnabled(false)
            .build()

//Avvia l'attività di autenticazione con l'intento creato precedentemente,
// consentirà di eseguire il login tramite i provider forniti
        signInLauncher.launch(signInIntent)

         //}
        val database =
            Firebase.database("https://opentrivia-fd778-default-rtdb.europe-west1.firebasedatabase.app/")



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
                        val fragmentManager = supportFragmentManager
                        val fragmentTransaction = fragmentManager.beginTransaction()
                        val fragment = ListaAmiciFragment()
                        fragmentTransaction.replace(R.id.fragmentContainerView2, fragment).addToBackStack("Menu").commit()
                        return true
                    }
                    R.id.inbox -> {
                        // Gestisci l'evento per l'item "Profilo" (inbox)
                        return true
                    }
                    R.id.preferiti -> {
                        // Gestisci l'evento per l'item "Statistiche" (preferiti)
                       return true
                    }
                    R.id.lista_amici -> {
                        val intent = Intent(this@MainActivity, ListaAmiciActivity::class.java)
                        startActivity(intent)
                    return true
                    }
                }
                return false
            }
        })


    }

    //Questo metodo viene chiamato quando il processo di autenticazione è completato.
    // Se l'autenticazione ha avuto successo (RESULT_OK), l'utente è stato correttamente autenticato
    // e l'oggetto FirebaseUser può essere ottenuto,
    //(può essere ottenuto utilizzando FirebaseAuth.getInstance().currentUser)
    @Override
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            // Mettere uid, name, email dentro onSignInResult
            //uid, nome ed email dell'utente autenticato tramite Firebase Auth
            val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
            val name = FirebaseAuth.getInstance().currentUser?.displayName.toString()
            val email = FirebaseAuth.getInstance().currentUser?.email.toString()
            //creiamo un oggetto User, utilizzando i metodi definiti in UserMethods
            userMethods = UserMethods()
            userMethods.initializeDatabase()
            userMethods.writeNewUser(uid, name, email)
        } else {
            Toast.makeText(
                this,
                "There was an error signing in",
                Toast.LENGTH_LONG
            ).show()

            val response = result.idpResponse
            if (response == null) {
                Log.w(TAG, "Sign in canceled")
            } else {
                Log.w(TAG, "Sign in error", response.error)
            }
        }
    }


    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        OpenTriviaTheme {
            Greeting("Android")
        }
    }
}

