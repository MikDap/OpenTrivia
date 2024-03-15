package com.example.opentrivia.utils

import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.fragment.app.FragmentManager
import com.example.opentrivia.gioco.AttendiTurnoFragment
import com.example.opentrivia.gioco.PareggioFragment
import com.example.opentrivia.gioco.SconfittaFragment
import com.example.opentrivia.gioco.VittoriaFragment
import java.util.Random

class GiocoUtils {
    companion object{

        fun schermataAttendi(fragmentManager: FragmentManager, containerId: Int) {
            val fragment = AttendiTurnoFragment()
            Handler(Looper.getMainLooper()).postDelayed({
                fragmentManager.beginTransaction()
                    .replace(containerId, fragment).commit()
            }, 500)
        }

        fun schermataVittoria(
            fragmentManager: FragmentManager,
            containerId: Int,
            nomeAvv: String,
            scoreMio: Int,
            scoreAvv: Int,
            mod: String
        ) {
            val fragment = VittoriaFragment().apply {
                this.nomeAvv = nomeAvv
                this.scoreMio = scoreMio.toString()
                this.scoreAvv = scoreAvv.toString()
                this.mod = mod
            }
            Handler(Looper.getMainLooper()).postDelayed({
                fragmentManager.beginTransaction()
                    .replace(containerId, fragment).commit()
            }, 500)
        }


        fun schermataPareggio(
            fragmentManager: FragmentManager,
            containerId: Int,
            nomeAvv: String,
            scoreMio: Int,
            scoreAvv: Int,
            mod: String
        ) {
            val fragment = PareggioFragment().apply {
                this.nomeAvv = nomeAvv
                this.scoreMio = scoreMio.toString()
                this.scoreAvv = scoreAvv.toString()
                this.mod = mod
            }
            Handler(Looper.getMainLooper()).postDelayed({
                fragmentManager.beginTransaction()
                    .replace(containerId, fragment).commit()
            }, 500)
        }


        fun schermataSconfitta(
            fragmentManager: FragmentManager,
            containerId: Int,
            nomeAvv: String,
            scoreMio: Int,
            scoreAvv: Int,
            mod: String
        ) {
            val fragment = SconfittaFragment().apply {
                this.nomeAvv = nomeAvv
                this.scoreMio = scoreMio.toString()
                this.scoreAvv = scoreAvv.toString()
                this.mod = mod
            }
            Handler(Looper.getMainLooper()).postDelayed({
                fragmentManager.beginTransaction()
                    .replace(containerId, fragment).commit()
            }, 500)
        }



        // in base al topic ricevuto, restituisco i numeri dedicati al topic (specificati in OpenTriviaDB)
        fun getCategoria(topic: String): String {
            lateinit var categoria: String
            val categorie_culturaPop = arrayOf("9","10","11","12","13","14","15","16","29","31","32")
            val categorie_scienze = arrayOf("17","18","19","30")
            when (topic) {
                "culturaPop" -> { categoria = getRandomTopic(categorie_culturaPop) }
                "sport" -> {categoria = "21"}
                "storia" ->{categoria = "23"}
                "geografia" -> {categoria= "22"}
                "arte" -> {categoria = "25"}
                "scienze" -> {categoria = getRandomTopic(categorie_scienze)}
                else -> throw IllegalArgumentException("Argomento non valido: $topic")
            }
            return categoria
        }


        //ritorna una categoria random
        fun getRandomTopic(topics: Array<String>): String {
            val randomIndex = Random().nextInt(topics.size)
            return topics[randomIndex]
        }


        //verifa se questa è la risposta corretta, restituisce true o false
        fun QuestaèLaRispostaCorretta(risposta: Button, rispostaCorretta: String): Boolean {


            if (risposta.text == rispostaCorretta) {
                risposta.setBackgroundColor(Color.LTGRAY)
                Handler(Looper.getMainLooper()).postDelayed({
                    risposta.setBackgroundColor(Color.GREEN)
                }, 500)
                return true
            } else {
                risposta.setBackgroundColor(Color.LTGRAY)
                Handler(Looper.getMainLooper()).postDelayed({
                    risposta.setBackgroundColor(Color.RED)
                }, 500)
                return false
            }
        }

        fun evidenziaRispostaCorretta(listaRisposte: List<Button>, rispostaCorretta: String) {


            listaRisposte.forEachIndexed { index, risposta ->
                if (risposta.text == rispostaCorretta) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        risposta.setBackgroundColor(Color.GREEN)
                    }, 500)
                }
            }
        }
    }
}