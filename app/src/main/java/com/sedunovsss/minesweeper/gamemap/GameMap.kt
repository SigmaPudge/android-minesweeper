package com.sedunovsss.minesweeper.gamemap
import android.graphics.Color
import android.widget.Button
import kotlin.random.Random

class GameMap (gameButtons: Array<Array<Button>>) {
    private var isGameRunning = false
    private val colors: Array<Int> = arrayOf(Color.rgb(0, 0, 255), Color.rgb(0, 255, 0), Color.rgb(255, 0, 0),
        Color.rgb(0, 0, 128), Color.rgb(128, 0, 0), Color.rgb(0, 128, 0), Color.rgb(0, 0, 0),
        Color.rgb(128, 0, 128))
    private var gameMap: Array<Array<Int>> = Array(10) { Array(10) { 0 } }
    private var isMapInit = false
    private var revealedCells = Array(8) { BooleanArray(8) { false } }
    private var setFlags = 0
    private var setOfFlags = mutableSetOf<Pair<Int, Int>>()
    private var buttons = gameButtons
    private val flagSymbol = "\uD83D\uDEA9"
    private val bombSymbol = "\uD83D\uDCA3"
    private var win = false
    private var lose = false

    private fun generateMap(): Array<Array<Int>> {
        val maybeGameMap: Array<Array<Int>> = Array(10) { Array(10) { 0 } }
        var countMines = 0
        while (countMines < 10){
            var x = Random.nextInt(1, 9)
            var y = Random.nextInt(1, 9)

            while (maybeGameMap[y][x] != 0) {
                x = Random.nextInt(1, 9)
                y = Random.nextInt(1, 9)
            }
            maybeGameMap[y][x] = -1
            countMines += 1
        }
        for (i in 1..8){
            for (j in 1..8){
                if (maybeGameMap[i][j] != -1){
                    for (z1 in -1..1){
                        for (z2 in -1..1){
                            if (!(z1 == 0 && z2 == 0)){
                                if (maybeGameMap[i+z1][j+z2] == -1){
                                    maybeGameMap[i][j] += 1
                                }
                            }
                        }
                    }
                }
            }
        }
        return maybeGameMap
    }
    private fun initMap(x: Int, y: Int){
        gameMap = generateMap()
        while (gameMap[y+1][x+1] != 0){
            gameMap = generateMap()
        }
        isMapInit = true
        isGameRunning = true
    }
    private fun getNumFromCell(x: Int, y: Int): Int{
        return gameMap[y+1][x+1]
    }
    private fun isMine(x: Int, y: Int): Boolean{
        if (isMapInit){
            return getNumFromCell(x, y) == -1
        }
        return false
    }
    private fun isEmpty(x: Int, y: Int): Boolean{
        if (isMapInit){
            return getNumFromCell(x, y) == 0
        }
        return false
    }
    private fun revealEmptyCells(startX: Int, startY: Int) {
        val queue = ArrayDeque<Pair<Int, Int>>()
        queue.add(startY to startX)
        while (queue.isNotEmpty()) {
            val (y, x) = queue.removeFirst()
            if (revealedCells[y][x]) continue
            revealedCells[y][x] = true
            openCell(x, y)
            if (isEmpty(x, y)) {
                for (dy in -1..1) {
                    for (dx in -1..1) {
                        if (dx == 0 && dy == 0) continue
                        val newY = y + dy
                        val newX = x + dx
                        if (newY in 0..7 && newX in 0..7) {
                            if (!revealedCells[newY][newX] && getNumFromCell(newX, newY) != -1 && newY to newX !in setOfFlags) {
                                queue.add(newY to newX)
                            }
                        }
                    }
                }
            }
        }
    }
    fun openCell(x: Int, y: Int){
        if (!isMapInit){
            initMap(x, y)
            revealEmptyCells(x, y)
        }
        else{
            if (isGameRun()){
                if (y to x !in setOfFlags){
                    if (!isMine(x, y)){
                        buttons[y][x].setBackgroundColor(Color.LTGRAY)
                        if (isEmpty(x, y)){
                            buttons[y][x].text = ""
                            revealEmptyCells(x, y)
                        }
                        else{
                            buttons[y][x].text = getNumFromCell(x, y).toString()
                            buttons[y][x].setTextColor(colors[getNumFromCell(x, y)-1])
                            revealedCells[y][x] = true
                        }
                    }
                    else{
                        fillCellsIfGameOver(x, y)
                        lose = true
                    }
                }
            }
        }
    }
    fun setFlag(x: Int, y: Int){
        if (isInitMap() && isGameRun()){
            if (!revealedCells[y][x]){
                buttons[y][x].setTextColor(Color.RED)
                if (y to x in setOfFlags){
                    setOfFlags.remove(y to x)
                    setFlags -= 1
                    buttons[y][x].text = ""
                }
                else{
                    if (setFlags < 10){
                        setOfFlags.add(y to x)
                        buttons[y][x].text = flagSymbol
                        setFlags += 1
                    }
                }
            }
        }
    }
    private fun fillCellsIfGameOver(x: Int, y: Int){
        if (x != -1 && y != -1){
            buttons[y][x].setBackgroundColor(Color.RED)
            buttons[y][x].setTextColor(Color.BLACK)
            buttons[y][x].text = bombSymbol
        }
        for (i in 0..7){
            for (j in 0..7){
                if (i != y || j != x){
                    if (gameMap[i+1][j+1] == -1 && i to j !in setOfFlags){
                        buttons[i][j].setTextColor(Color.WHITE)
                        buttons[i][j].text = bombSymbol
                        buttons[i][j].setBackgroundColor(Color.rgb(255, 0, 255))
                    }
                    if (gameMap[i+1][j+1] != -1 && (i to j in setOfFlags)){
                        buttons[i][j].setTextColor(Color.WHITE)
                        buttons[i][j].setBackgroundColor(Color.RED)
                    }
                    if (gameMap[i+1][j+1] == -1 && (i to j in setOfFlags)){
                        buttons[i][j].setTextColor(Color.WHITE)
                        buttons[i][j].setBackgroundColor(Color.GREEN)
                    }
                }
            }
        }
    }
    private fun checkWIn(){
        var minesError = 0
        var emptyCells = 0
        for (i in 0..7){
            for (j in 0..7){
                if (i to j in setOfFlags){
                    if (getNumFromCell(j, i) != -1){
                        minesError += 1
                    }
                }
                else{
                    if (!revealedCells[i][j]){
                        emptyCells += 1
                    }
                }
            }
        }
        if (emptyCells == 0 && minesError == 0){
            win = true
            isGameRunning = false
            fillCellsIfGameOver(-1, -1)
        }
    }
    fun isGameRun(): Boolean{
        return isGameRunning
    }
    fun isWin(): Boolean{
        checkWIn()
        return win
    }
    fun isLose(): Boolean{
        if (lose){
            isGameRunning = false
        }
        return lose
    }
    fun isInitMap(): Boolean{
        return isMapInit
    }
    fun getCountFlags(): Int{
        return setFlags
    }
}