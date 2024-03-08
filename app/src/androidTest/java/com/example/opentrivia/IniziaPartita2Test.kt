package com.example.opentrivia
import android.app.Activity
import android.app.Instrumentation.ActivityMonitor
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.example.opentrivia.gioco.argomento_singolo.ModArgomentoActivity
import com.example.opentrivia.menu.IniziaPartita
import junit.framework.TestCase.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.matcher.ViewMatchers.withText


@RunWith(AndroidJUnit4::class)
class IniziaPartita2Test {
    @Test
    fun testEventFragment() {
        val activityMonitor: ActivityMonitor =
            getInstrumentation().addMonitor(ModArgomentoActivity::class.java.name, null, false)
        // The "fragmentArgs" argument is optional.
        val bundle = Bundle().apply {
            putString("modalita", "argomento singolo")
            putString("difficolta", "difficile")
            putString("selezione", "casuale")
        }
        val scenario = launchFragmentInContainer<IniziaPartita>(bundle, initialState = Lifecycle.State.INITIALIZED)
        scenario.moveToState(Lifecycle.State.RESUMED)
        onView(withId(R.id.cercaPartitaButton)).perform(click())
        val nextActivity: Activity? =
            getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000)
        assertNotNull(nextActivity)
        nextActivity?.finish()
      /*  onView(withId(R.id.sport)).perform(click())


        onView(withId(R.id.domanda)).check(matches(isDisplayed()))
        val domandaPrima = onView(withId(R.id.domanda)).toString()
        onView(withId(R.id.risposta1)).perform(click())
        onView(withId(R.id.domanda)).check(matches(isDisplayed())).check(matches(not(withText(domandaPrima))))
       */
    }

    @Test
    fun testModArgomentoActivity() {

// Avvia ModArgomentoActivity
        val scenario = ActivityScenario.launch(ModArgomentoActivity::class.java)

        var activity1: ModArgomentoActivity? = null
        val activityMonitor: ActivityMonitor = getInstrumentation().addMonitor(MenuActivity::class.java.name, null, false)

        // Crea un CountDownLatch con un contatore di 1
        val latch = CountDownLatch(1)
        // Esegui le operazioni di test
        scenario.onActivity { activity ->
            // Interagisci con gli elementi dell'Activity e i suoi fragment

             activity1 = activity

            Log.d("ActivityTest", "Activity restituita: ${activity.javaClass.simpleName}")


                }

        scenario.moveToState(Lifecycle.State.RESUMED)
        onView(withId(R.id.sport_arg)).perform(click())

        Log.d("entra123", "si")

    //    val fragmentManager = activity1?.supportFragmentManager
     //   val fragment = fragmentManager?.findFragmentById(R.id.fragmentContainerViewGioco2)



        // Verifica se il fragment è di tipo SceltaMultiplaArgSingolo
      //  if (fragment is SceltaMultiplaFragmentArgSingolo) {

            Log.d("entra1234", "si")
            // Ora puoi accedere alle variabili e metodi del fragment direttamente
            // Esempio di accesso a una variabile del fragment:
            val question = "Nuova domanda"
            val answers = listOf("Risposta 1", "Risposta 2", "Risposta 3", "Risposta 4")

            // Imposta la domanda e le risposte utilizzando gli onView dei button
          //  onView(withId(R.id.domanda_arg)).perform(replaceText(question))

        latch.await(4, TimeUnit.SECONDS)
        val rispostaCorretta =activity1?.rispostaCorretta

            onView(withId(R.id.risposta1_arg)).perform(click())
         //   onView(withId(R.id.risposta2_arg)).perform(replaceText(answers[1]))
            val button = activity1?.findViewById<Button>(R.id.risposta1_arg)

       //    assertEquals(rispostaCorretta, button?.text.toString())
       // }


        // Aspetta che il contatore del CountDownLatch raggiunga 0 o fino a 6 secondi
        latch.await(3, TimeUnit.SECONDS)


        pressBack()

// Verifica che il dialogo sia stato visualizzato
        latch.await(2, TimeUnit.SECONDS)
        onView(withText("Vuoi ritornare al menù?")).check(matches(isDisplayed()))

        // Simula il clic sul pulsante "NO" (senza await perchè il perform aspetta implicitamente che la view sia visibile
        onView(withText("NO")).perform(click())

        // Verifica che il dialogo sia stato chiuso
        onView(withText("Vuoi ritornare al menù?")).check(doesNotExist())

        pressBack()

// Verifica che il dialogo sia stato visualizzato
        latch.await(2, TimeUnit.SECONDS)
        onView(withText("Vuoi ritornare al menù?")).check(matches(isDisplayed()))

        // Simula il clic sul pulsante "NO" (senza await perchè il perform aspetta implicitamente che la view sia visibile
        onView(withText("SI")).perform(click())

        latch.await(3, TimeUnit.SECONDS)

        val mainActivity2: Activity? = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000)
        assertNotNull(mainActivity2)

        latch.await(3, TimeUnit.SECONDS)
    }
}