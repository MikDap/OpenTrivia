package com.example.opentrivia

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.opentrivia.ui.theme.OpenTriviaTheme


class MainActivity : ComponentActivity()  {

    private lateinit var userMethods: UserMethods
    private lateinit var image: ImageView
    private lateinit var animation: AnimatedVectorDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        image = findViewById<View>(R.id.avd) as ImageView
    //    val progressBar = findViewById<ProgressBar>(R.id.progressBar)

    //    progressBar.visibility = ProgressBar.VISIBLE

        userMethods = UserMethods()

/*
        //MENU
        if (userMethods.checkUtenteisLoggato()) {
            val intent = Intent(this@MainActivity, MenuActivity::class.java)
            startActivity(intent)
            finish()



        }
        //LOGIN
        else {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
 */
    }
    override fun onStart() {
        super.onStart()
        val d = image.drawable
        if (d is AnimatedVectorDrawable) {
            Log.d("testanim", "onCreate: instancefound$d")
            animation = d
            animation.start()
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