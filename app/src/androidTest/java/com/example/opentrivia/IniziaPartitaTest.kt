package com.example.opentrivia

import android.os.Bundle
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.example.opentrivia.gioco.a_tempo.ModATempoActivity
import com.example.opentrivia.gioco.argomento_singolo.ModArgomentoActivity
import com.example.opentrivia.gioco.classica.ModClassicaActivity
import com.example.opentrivia.menu.IniziaPartita
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IniziaPartitaTest {
    private lateinit var scenario: ActivityScenario<MenuActivity>
    private lateinit var modalita: String
    private lateinit var difficolta: String
    private lateinit var selezione: String

    @Before
    fun setUp() {
        scenario = launch(MenuActivity::class.java)
      scenario.moveToState(Lifecycle.State.RESUMED)
        scenario.onActivity { activity ->
            val fragment = IniziaPartita()
            val bundle = Bundle().apply {
                putString("modalita", "classica")
                putString("difficolta", "difficile")
                putString("selezione", "casuale")
            }
            fragment.arguments = bundle
            activity.supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainerView2, fragment)
                .commit()

            modalita = bundle.getString("modalita", "")
            difficolta = bundle.getString("difficolta", "")
            selezione = bundle.getString("selezione", "")
        }
    }

        @Test
        fun testStartActivityBasedOnMode() {

            // Fai clic sul pulsante "cercaPartitaButton"
            onView(withId(R.id.cercaPartitaButton)).perform(click())


            // Controlla se l'intento corretto è stato avviato in base alla modalità selezionata
            when (modalita) {
                "classica" -> {
                    intended(hasComponent(ModClassicaActivity::class.java.name))
                }

                "argomento singolo" -> {
                    intended(hasComponent(ModArgomentoActivity::class.java.name))
                }

                "a tempo" -> {
                    intended(hasComponent(ModATempoActivity::class.java.name))
                }
            }
        }

}
