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
import com.example.opentrivia.gioco.classica.ModClassicaActivity
import com.example.opentrivia.menu.IniziaPartita
import junit.framework.TestCase.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class IniziaPartita2Test {
    @Test
    fun testEventFragment() {
        val activityMonitor: ActivityMonitor =
            getInstrumentation().addMonitor(ModClassicaActivity::class.java.name, null, false)
        // The "fragmentArgs" argument is optional.
        val bundle = Bundle().apply {
            putString("modalita", "classica")
            putString("difficolta", "difficile")
            putString("selezione", "casuale")
        }
        val scenario = launchFragmentInContainer<IniziaPartita>(bundle, initialState = Lifecycle.State.INITIALIZED)
        //scenario.moveToState(Lifecycle.State.RESUMED)
        onView(withId(R.id.cercaPartitaButton)).perform(click())

        val nextActivity: Activity? =
            getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000)
        assertNotNull(nextActivity)
        nextActivity?.finish()
    }
}