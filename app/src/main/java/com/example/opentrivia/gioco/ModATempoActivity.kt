package com.example.opentrivia.gioco

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.opentrivia.R

class ModATempoActivity : AppCompatActivity() {

    private lateinit var partita: String
    private lateinit var difficolta: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mod_a_tempo)

        partita = intent.getStringExtra("partita") ?: ""
        difficolta = intent.getStringExtra("difficolta") ?: ""
    }


}