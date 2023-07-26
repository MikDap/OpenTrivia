package com.example.opentrivia.gioco

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.opentrivia.R
import com.example.opentrivia.api.ChiamataApi

class ModClassicaActivity : AppCompatActivity() {

    private lateinit var partita: String
    private lateinit var difficolta: String
    private lateinit var chiamataApi: ChiamataApi



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mod_classica)

        partita = intent.getStringExtra("partita") ?: ""
        difficolta = intent.getStringExtra("difficolta") ?: ""







    }


}