package com.sedunovsss.minesweeper

import android.annotation.SuppressLint
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import java.util.Locale
import android.util.Log

class DataActivity : AppCompatActivity() {
    private lateinit var back2DaGame: ImageButton
    private lateinit var wins: TextView
    private lateinit var loses: TextView
    private lateinit var winRate: TextView
    private lateinit var bestTime: TextView
    private lateinit var meanTime: TextView
    private lateinit var vibrate: CheckBox
    private lateinit var prefs: SharedPreferences

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data)
        back2DaGame = findViewById(R.id.back2DaGame)
        wins = findViewById(R.id.wins)
        loses = findViewById(R.id.loses)
        winRate = findViewById(R.id.winRate)
        bestTime = findViewById(R.id.bestTime)
        meanTime = findViewById(R.id.meanTime)
        vibrate = findViewById(R.id.checkBox)
        prefs = getSharedPreferences("game_prefs", MODE_PRIVATE)
        back2DaGame.setOnClickListener {
            finish()
        }
        vibrate.isChecked = prefs.getBoolean("vibrate", true)
        vibrate.setOnClickListener{
            prefs.edit().putBoolean("vibrate", !prefs.getBoolean("vibrate", true)).apply()
        }
        wins.text = "Wins: " + prefs.getLong("wins", 0L).toString()
        loses.text = "Loses: "  + prefs.getLong("loses", 0L).toString()
        val winRateStr: Double = if (prefs.getLong("wins", 0L) + prefs.getLong("loses", 0L) > 0) {
            prefs.getLong("wins", 0L).toDouble() / (prefs.getLong("wins", 0L) + prefs.getLong("loses", 0L)).toDouble() * 100.0
        } else {
            0.0
        }
        val formattedWinRate = String.format(Locale.US, "%.2f", winRateStr)
        winRate.text = "WinRate: $formattedWinRate%"
        val bt = prefs.getLong("best_time", Long.MAX_VALUE)
        if (bt == Long.MAX_VALUE){
            bestTime.text = "Best time: None"
        }
        else{
            val bts = (bt.toDouble() / 1000).toString()
            bestTime.text = "Best time: $bts S"
        }
        val mt = prefs.getLong("mean_time", Long.MAX_VALUE)
        Log.d("MeanTime", mt.toString())
        if (mt == Long.MAX_VALUE){
            meanTime.text = "Mean time: None"
        }
        else{
            val mts = (mt.toDouble() / 1000).toString()
            meanTime.text = "Mean time: $mts S"
        }
    }
}