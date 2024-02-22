package com.example.opentrivia.utils

import android.os.Handler
import android.os.Looper
import androidx.fragment.app.FragmentManager
import com.example.opentrivia.gioco.AttendiTurnoFragment
import com.example.opentrivia.gioco.Pareggio
import com.example.opentrivia.gioco.Sconfitta
import com.example.opentrivia.gioco.Vittoria
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
            val fragment = Vittoria().apply {
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
            val fragment = Pareggio().apply {
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
            val fragment = Sconfitta().apply {
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
    }
}