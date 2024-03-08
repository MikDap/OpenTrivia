package com.example.opentrivia

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class LoginActivity : AppCompatActivity() {

    private lateinit var userMethods: UserMethods
    private lateinit var emailEditText: EditText
    private lateinit var nextButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

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
            .setTheme(R.style.LoginTheme)
            .build()

//Avvia l'attività di autenticazione con l'intento creato precedentemente,
// consentirà di eseguire il login tramite i provider forniti
        signInLauncher.launch(signInIntent)

    }


    //Questo metodo viene chiamato quando il processo di autenticazione è completato.
    // Se l'autenticazione ha avuto successo (RESULT_OK), l'utente è stato correttamente autenticato
    // e l'oggetto FirebaseUser può essere ottenuto,
    //(può essere ottenuto utilizzando FirebaseAuth.getInstance().currentUser)
    @Override
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {

            // Ottieni l'istanza di FirebaseAuth
            val auth = FirebaseAuth.getInstance()


            val uid = auth.currentUser?.uid.toString()
            val name = auth.currentUser?.displayName.toString()
            val email = auth.currentUser?.email.toString()


             //SE L'UTENTE NON C'è IN USER, LO SCRIVIAMO
            val usersRef = FirebaseDatabase.getInstance().getReference("users")
            usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(users: DataSnapshot) {
                    if (!users.hasChild(uid)) {
                        // Se l'UID dell'utente non è presente, scriviamo le informazioni dell'utente nel database
                        userMethods = UserMethods()
                        userMethods.initializeDatabase()
                        userMethods.writeNewUserOnDB(uid, name, email)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Gestione degli errori, se necessario
                }
            })


            val intent = Intent(this@LoginActivity, MenuActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(
                this,
                "There was an error signing in",
                Toast.LENGTH_LONG
            ).show()

            val response = result.idpResponse
            if (response == null) {
                Log.w(ContentValues.TAG, "Sign in canceled")
            } else {
                Log.w(ContentValues.TAG, "Sign in error", response.error)
            }
        }
    }

}
