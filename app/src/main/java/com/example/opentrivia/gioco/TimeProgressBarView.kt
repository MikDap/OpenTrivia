package com.example.opentrivia.gioco

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimeProgressBarView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paint = Paint()
    private var totalTimeInMillis = 0L
     var elapsedTimeInMillis = 0L

    init {
        paint.color = Color.BLUE
    }

    fun setTotalTimeAndStart(totalTime: Long) {
        totalTimeInMillis = totalTime
        elapsedTimeInMillis = 0
        startTimer()
    }

    private fun startTimer() {
        // Imposta un Timer o un Handler per aggiornare elapsedTimeInMillis e chiamare postInvalidate()
        // a intervalli regolari in base al tempo trascorso.
        val interval = 1000L // Aggiorna ogni secondo
        CoroutineScope(Dispatchers.Main).launch {
            while (elapsedTimeInMillis < totalTimeInMillis) {
                elapsedTimeInMillis += interval
                postInvalidate()
                delay(interval)
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val progress = (elapsedTimeInMillis.toFloat() / totalTimeInMillis.toFloat())
        val width = width.toFloat()
        val height = height.toFloat()
        canvas?.drawRect(0f, 0f, width * progress, height, paint)
    }
}

