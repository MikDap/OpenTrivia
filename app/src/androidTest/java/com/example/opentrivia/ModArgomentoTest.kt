package com.example.opentrivia
import android.app.Activity
import android.app.Instrumentation.ActivityMonitor
import android.os.Bundle
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
import com.example.opentrivia.menu.MenuActivity


@RunWith(AndroidJUnit4::class)
class ModArgomentoTest {

    // L'obiettivo di questo test è verificare che, quando si preme un pulsante specifico (cercaPartitaButton)
    // all'interno del fragment IniziaPartita, venga avviata un'activity specifica (ModArgomentoActivity)
    // con i dati corretti passati attraverso un bundle.
    @Test
    fun testIniziaPartitaFragment() {

        //per monitorare l'apertura dell'activity ModArgomentoActivity.
        val activityMonitor: ActivityMonitor = getInstrumentation().addMonitor(ModArgomentoActivity::class.java.name, null, false)
        //Bundle richiesto dal fragment IniziaPartita
        val bundle = Bundle().apply {
            putString("modalita", "argomento singolo")
            putString("difficolta", "difficile")
            putString("selezione", "casuale")
        }
        //Avviamo IniziaPartita passandogli il bundle
        val scenario = launchFragmentInContainer<IniziaPartita>(bundle, initialState = Lifecycle.State.INITIALIZED)
        scenario.moveToState(Lifecycle.State.RESUMED)

        //clicchiamo il tasto per cercare la partita
        onView(withId(R.id.cercaPartitaButton)).perform(click())

        //Viene atteso che l'activity ModArgomentoActivity venga avviata
        val nextActivity: Activity? = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000)
        //Viene verificato che l'activity sia stata avviata correttamente (non sia null).
        assertNotNull(nextActivity)
        nextActivity?.finish()
    }


    //Questo test verifica il comportamento dell'applicazione quando si interagisce con ModArgomentoActivity,
    // in particolare controlla il funzionamento dell'AlertDialog e il corretto flusso di navigazione
    // verso MenuActivity. Utilizza CountDownLatch per sincronizzare il test
    // con i tempi di esecuzione delle azioni nell'applicazione.
    @Test
    fun testModArgomentoActivity() {

        // Avvia ModArgomentoActivity
        val scenario = ActivityScenario.launch(ModArgomentoActivity::class.java)

        //per monitorare l'activityMenuActivity
        val activityMonitor: ActivityMonitor = getInstrumentation().addMonitor(MenuActivity::class.java.name, null, false)

        // Crea un CountDownLatch
        val latch = CountDownLatch(1)

        scenario.moveToState(Lifecycle.State.RESUMED)

        //Simula un click scegliendo sport come topic.
        onView(withId(R.id.sport_arg)).perform(click())

        latch.await(4, TimeUnit.SECONDS)

        //Simula il pressione del pulsante indietro (Back)
        pressBack()

        latch.await(1, TimeUnit.SECONDS)

        // Verifica che il dialogo sia stato visualizzato
        onView(withText("Vuoi ritornare al menù?")).check(matches(isDisplayed()))

        // Simula il clic sul pulsante "NO" (senza await perchè il perform aspetta implicitamente che la view sia visibile
        onView(withText("NO")).perform(click())

        // Verifica che il dialogo sia stato chiuso
        onView(withText("Vuoi ritornare al menù?")).check(doesNotExist())

        //Simula il pressione del pulsante indietro (Back)
        pressBack()

        latch.await(1, TimeUnit.SECONDS)

        // Verifica che il dialogo sia stato visualizzato
        onView(withText("Vuoi ritornare al menù?")).check(matches(isDisplayed()))

        // Simula il clic sul pulsante "NO" (senza await perchè il perform aspetta implicitamente che la view sia visibile
        onView(withText("SI")).perform(click())

        latch.await(3, TimeUnit.SECONDS)

        //Aspetta che l'ActivityMonitor rilevi l'apertura di MenuActivity
        val menuActivity: Activity? = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000)
        //Verifica che MenuActivity non sia nulla.
        assertNotNull(menuActivity)
        menuActivity?.finish()
    }
}