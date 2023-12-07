package com.example.opentrivia.gioco
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
class TimeProgressBarView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paint = Paint()
    var timeStamp = 0L
    var finalTime = 0L
    var associatedFragment: VeroFalsoFragment? = null // Variabile per il fragment associato
    private var coroutine =  CoroutineScope(Dispatchers.Main)
    private var stoppedTimer = false

    fun startTimer() {
        // Imposta un Timer o un Handler per aggiornare elapsedTimeInMillis e chiamare postInvalidate()
        // a intervalli regolari in base al tempo trascorso.
        val interval = 1000L // Aggiorna ogni secondo
        coroutine.launch {
            while (timeStamp < finalTime) {
                timeStamp = System.currentTimeMillis()
                postInvalidate()
                delay(interval)
            }
            if (!stoppedTimer) {
                associatedFragment?.finePartita()
                Log.d("finePartita","si")
            }
        }
    }
    private val progressColors = Color.parseColor("#FF7514")
    private val totalTimeColor = Color.parseColor("#F4F4F4")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val differenza = finalTime - timeStamp
        val tempoPassato = 60000L - differenza
        val progress = tempoPassato.toFloat() / 60000L.toFloat()
        val width = width.toFloat()
        val height = height.toFloat()
        // Disegna la barra del tempo totale
        val totalTimeBarWidth = width
        paint.color = totalTimeColor
        canvas?.drawRect(0f, 0f, totalTimeBarWidth, height, paint)
        // Disegna la barra di avanzamento
        val progressBarWidth = width * progress
        paint.color = progressColors
        canvas?.drawRect(0f, 0f, progressBarWidth, height, paint)
    }
    fun stopTimer() {
        stoppedTimer = true
        coroutine.cancel()
    }
}
 