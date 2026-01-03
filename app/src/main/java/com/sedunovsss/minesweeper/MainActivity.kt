package com.sedunovsss.minesweeper
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.os.Vibrator
import android.content.SharedPreferences
import com.sedunovsss.minesweeper.timer.Timer
import com.sedunovsss.minesweeper.gamemap.GameMap
import android.widget.ImageButton

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var buttons: Array<Array<Button>>
    private lateinit var minesCountView: TextView
    private lateinit var replayButton: Button
    private lateinit var timerLabel: TextView
    private lateinit var prefs: SharedPreferences
    private lateinit var timer: Timer
    private lateinit var gm: GameMap
    private lateinit var vibrator: Vibrator
    private lateinit var infoButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        prefs = getSharedPreferences("game_prefs", MODE_PRIVATE)
        initializeButtons()
        minesCountView = findViewById(R.id.minesCountLabel)
        replayButton = findViewById(R.id.replayButton)
        replayButton.setOnClickListener{resetGame()}
        timerLabel = findViewById(R.id.timerLabel)
        infoButton = findViewById(R.id.infoButton)
        timer = Timer(timerLabel)
        gm = GameMap(buttons)
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        timer.updateTimer()
        infoButton.setOnClickListener{
            val intent = Intent(this, DataActivity::class.java)
            startActivity(intent)
        }
    }
    @SuppressLint("DiscouragedApi")
    private fun initializeButtons() {
        buttons = Array(8) { row ->
            Array(8) { col ->
                val buttonId = resources.getIdentifier(
                    "button${row * 8 + col + 1}",
                    "id",
                    packageName
                )
                findViewById(buttonId)
            }
        }
        for (i in 0..7){
            for (j in 0..7){
                buttons[i][j].text = ""
                buttons[i][j].setBackgroundColor(Color.GRAY)
                buttons[i][j].setOnClickListener{shortClick(j, i)}
                buttons[i][j].setOnLongClickListener{longClick(j, i); true}
            }
        }
    }
    private fun vibrate(milliseconds: Long){
        if (prefs.getBoolean("vibrate", true)){
            vibrator.vibrate(milliseconds)
        }
    }
    private fun shortClick(x: Int, y: Int){
        if (!gm.isInitMap()){
            timer.startTimer()
        }
        gm.openCell(x, y)
        if (gm.isGameRun()){
            if (gm.isWin()){
                winFunction()
            }
            else if (gm.isLose()){
                loseFunction()
            }
            else{
                vibrate(100)
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private fun longClick(x: Int, y: Int){
        gm.setFlag(x, y)
        if (gm.isGameRun()){
            if (gm.isWin()){
                winFunction()
            }
            else{
                vibrate(100)
            }
        }
        minesCountView.text = "Mines: " + (10-gm.getCountFlags()).toString()
    }
    @SuppressLint("SetTextI18n")
    private fun resetGame(){
        timer.resetTimer()
        initializeButtons()
        gm = GameMap(buttons)
        minesCountView.text = "Mines: " + (10-gm.getCountFlags()).toString()
    }
    private fun winFunction(){
        timer.stopTimer()
        if (prefs.getLong("best_time", Long.MAX_VALUE) > timer.getElapsedTime()){
            prefs.edit().putLong("best_time", timer.getElapsedTime()).apply()
            Toast.makeText(this,"You won! New best time: " + (timer.getElapsedTime() / 1000).toString() + "!", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(this,"You won!", Toast.LENGTH_SHORT).show()
        }
        val wins = prefs.getLong("wins", 0L)
        val meanTime = prefs.getLong("mean_time", 0L)
        val newMeanTime = (wins*meanTime + timer.getElapsedTime()) / (wins+1L)
        prefs.edit().putLong("mean_time", newMeanTime).apply()
        prefs.edit().putLong("wins", wins+1L).apply()
        vibrate(200)
    }
    private fun loseFunction(){
        timer.stopTimer()
        Toast.makeText(this,"Game Over!", Toast.LENGTH_SHORT).show()
        val loses = prefs.getLong("loses", 0L)
        prefs.edit().putLong("loses", loses+1L).apply()
        vibrate(200)
    }
}