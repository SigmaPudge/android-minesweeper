package com.sedunovsss.minesweeper.timer
import android.os.Handler
import android.os.Looper
import android.widget.TextView

class Timer (TimerLabel: TextView) {
    private var startTime = 0L
    private var elapsedTime = 0L
    private var isTimerRunning = false
    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval = 100L
    private var timerLabel: TextView = TimerLabel

    private val timerRunnable = object : Runnable {
        override fun run() {
            if (isTimerRunning) {
                val currentTime = System.currentTimeMillis()
                elapsedTime = currentTime - startTime
                updateTimer()
                handler.postDelayed(this, updateInterval)
            }
        }
    }
    fun updateTimer() {
        val totalSeconds = elapsedTime / 1000
        timerLabel.text = totalSeconds.toString()
    }
    fun startTimer() {
        if (!isTimerRunning) {
            isTimerRunning = true
            startTime = System.currentTimeMillis() - elapsedTime
            handler.post(timerRunnable)
        }
    }
    fun stopTimer() {
        isTimerRunning = false
        handler.removeCallbacks(timerRunnable)
    }
    fun resetTimer() {
        stopTimer()
        elapsedTime = 0
        updateTimer()
    }
    fun getElapsedTime(): Long{
        return elapsedTime
    }
}