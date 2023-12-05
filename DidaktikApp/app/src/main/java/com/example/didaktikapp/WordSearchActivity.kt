package com.example.didaktikapp

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlin.random.Random

class WordSearchActivity: AppCompatActivity() {
    private lateinit var tableLayout: TableLayout
    private lateinit var myWordSearch: Array<Array<Char>>
    val size = 8
    private val wordList = listOf("ODOLA", "KIPULA", "PORRUA", "HESTEA", "KOIPEA")

    val wordMap: Map<String, MutableList<Pair<Int, Int>>> = mapOf(
        "ODOLA" to mutableListOf(),
        "KIPULA" to mutableListOf(),
        "PORRUA" to mutableListOf(),
        "HESTEA" to mutableListOf(),
        "KOIPEA" to mutableListOf()
    )


    private var startCoordinate: Pair<Float, Float>? = null
    private var newWord:String = ""
    private var selectedCells: MutableList<Int> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_search)

        tableLayout = findViewById(R.id.wordSearch)
        val layoutParams = tableLayout.layoutParams
        layoutParams.height = layoutParams.width
        tableLayout.layoutParams = layoutParams

        myWordSearch = Array(size) { Array(size) { ' ' } }
        CreateWordSearch()
        CreateGrid(tableLayout)


    }

    fun CreateWordSearch(){
        SetFirstWord()
        wordMap.keys.forEachIndexed { index, element ->
            if (index > 0) {
                var wordToCompareIndex = index-1
                while(HasCommonChar(element, wordMap.keys.elementAt(wordToCompareIndex)) == null && wordToCompareIndex >= 0){
                    wordToCompareIndex =- 1
                }
                val commonCharPos = HasCommonChar(element, wordMap.keys.elementAt(wordToCompareIndex))
                if (commonCharPos != null) {
                    val firstCommonPos = commonCharPos.first
                }
            }
        }

    }

    fun HasCommonChar(myCurrentWord: String, previousWord:String):Pair<Int,Int>?{
        for ((indexChar, char) in myCurrentWord.withIndex()) {
            for ((indexPreChar, preChar) in previousWord.withIndex()) {
                if (char == preChar) {
                    return Pair(indexChar, indexPreChar)
                }
            }
        }
        return null
    }

    fun SetFirstWord(){
        val numRows = myWordSearch.size
        val numCols = myWordSearch[0].size

        val randomRow = Random.nextInt(numRows)
        val randomCol = Random.nextInt(numCols)
        var randomDirection = Random.nextInt(Direction.values().size)
        //val randomChar = myWordSearch[randomRow][randomCol]

        while (!canWriteWord(randomRow, randomCol, wordList[0], Direction.values()[randomDirection])) {
            randomDirection = (randomDirection + 1) % Direction.values().size
        }
        writeWord(randomRow, randomCol, wordMap.keys.elementAt(0), Direction.values()[randomDirection], 0)
    }
    enum class Direction {
        HORIZONTAL,
        REVERSE_HORIZONTAL,
        VERTICAL_UP,
        VERTICAL_DOWN,
        DIAGONAL_UP_RIGHT,
        DIAGONAL_UP_LEFT,
        DIAGONAL_DOWN_RIGHT,
        DIAGONAL_DOWN_LEFT
    }
    private fun canWriteWord(startRow: Int, startCol: Int, word: String, direction:Direction):Boolean{
        when (direction) {
            Direction.HORIZONTAL -> if (startCol + word.length > size) return false
            Direction.REVERSE_HORIZONTAL -> if (startCol - word.length < 0) return false
            Direction.VERTICAL_DOWN -> if (startRow + word.length > size) return false
            Direction.VERTICAL_UP -> if (startRow - word.length < 0) return false
            Direction.DIAGONAL_UP_RIGHT -> if (startRow - word.length < 0 || startCol + word.length > size) return false
            Direction.DIAGONAL_UP_LEFT -> if (startRow - word.length < 0 || startCol - word.length < 0) return false
            Direction.DIAGONAL_DOWN_RIGHT -> if (startRow + word.length > size || startCol + word.length > size) return false
            Direction.DIAGONAL_DOWN_LEFT -> if (startRow + word.length > size || startCol - word.length < 0) return false
        }
        return true
    }

    fun writeWord(startRow: Int, startCol: Int, word: String, direction: Direction, startFromIndex: Int) {
        val coordinates = mutableListOf<Pair<Int, Int>>()

        for (i in word.indices) {
            val charIndex = startFromIndex + i
            when (direction) {
                Direction.HORIZONTAL -> {
                    myWordSearch[startRow][startCol + i] = word[charIndex]
                    coordinates.add(Pair(startRow, startCol + i))
                }
                Direction.REVERSE_HORIZONTAL -> {
                    myWordSearch[startRow][startCol - i] = word[charIndex]
                    coordinates.add(Pair(startRow, startCol - i))
                }
                Direction.VERTICAL_DOWN -> {
                    myWordSearch[startRow + i][startCol] = word[charIndex]
                    coordinates.add(Pair(startRow + i, startCol))
                }
                Direction.VERTICAL_UP -> {
                    myWordSearch[startRow - i][startCol] = word[charIndex]
                    coordinates.add(Pair(startRow - i, startCol))
                }
                Direction.DIAGONAL_UP_RIGHT -> {
                    myWordSearch[startRow - i][startCol + i] = word[charIndex]
                    coordinates.add(Pair(startRow - i, startCol + i))
                }
                Direction.DIAGONAL_UP_LEFT -> {
                    myWordSearch[startRow - i][startCol - i] = word[charIndex]
                    coordinates.add(Pair(startRow - i, startCol - i))
                }
                Direction.DIAGONAL_DOWN_RIGHT -> {
                    myWordSearch[startRow + i][startCol + i] = word[charIndex]
                    coordinates.add(Pair(startRow + i, startCol + i))
                }
                Direction.DIAGONAL_DOWN_LEFT -> {
                    myWordSearch[startRow + i][startCol - i] = word[charIndex]
                    coordinates.add(Pair(startRow + i, startCol - i))
                }
            }
        }
        wordMap[word]?.addAll(coordinates)
    }



    fun CreateGrid(tableLayout:TableLayout) {
        tableLayout.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    tableLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    val tableLayoutWidth = tableLayout.width
                    val cellSize = tableLayoutWidth / size

                    for (i in 0 until size) {
                        // Crea una nueva fila
                        val tableRow = TableRow(this@WordSearchActivity)

                        for (j in 0 until size) {
                            val frameLayout = FrameLayout(this@WordSearchActivity)

                            val layoutParams = TableRow.LayoutParams(cellSize, cellSize)
                            frameLayout.layoutParams = layoutParams
                            frameLayout.setPadding(15,15,15,15)
                            // Crear un TextView dentro del FrameLayout
                            val textView = TextView(this@WordSearchActivity)

                            val textLayoutParams = FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT
                            )
                            textView.layoutParams = textLayoutParams
                            textView.gravity = Gravity.CENTER
                            if(!myWordSearch[i][j].toString().equals("")){
                                textView.text = myWordSearch[i][j].toString()
                            } else{
                                textView.text = ""
                            }
                            //textView.text = "A"
                            val uniqueId = "R:$i C:$j"
                            textView.id = uniqueId.hashCode()

                            textView.setOnTouchListener { _, event ->
                                onTouchEvent(event)
                            }

                            frameLayout.addView(textView)
                            tableRow.addView(frameLayout)
                        }
                        tableLayout.addView(tableRow)
                    }
                }
            }
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startCoordinate = Pair(event.rawX, event.rawY)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                startCoordinate?.let { startCoord ->
                    for (i in 0 until tableLayout.childCount) {
                        val row = tableLayout.getChildAt(i) as TableRow
                        for (j in 0 until row.childCount) {
                            val frameLayout = row.getChildAt(j) as FrameLayout
                            val textView = frameLayout.getChildAt(0) as TextView

                            if (isPointInsideView(startCoord, textView, event.rawX, event.rawY)) {
                                if(!selectedCells.contains(textView.id)){
                                    newWord += textView.text
                                    selectedCells.add(textView.id)
                                    frameLayout.setBackgroundColor(ContextCompat.getColor(this@WordSearchActivity, R.color.verdeClaro))
                                }
                            }
                        }
                    }
                }
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                startCoordinate = null
                HandleClick()
                return true
            }
            else -> return false
        }
    }

    private fun HandleClick(){
        val toast = Toast.makeText(this, newWord, Toast.LENGTH_LONG)
        toast.show()

        for (i in 0 until tableLayout.childCount) {
            val row = tableLayout.getChildAt(i) as TableRow
            for (j in 0 until row.childCount) {
                val frameLayout = row.getChildAt(j) as FrameLayout
                val textView = frameLayout.getChildAt(0) as TextView

                selectedCells.forEach{code ->
                    if (code.equals(textView.id)){
                        frameLayout.setBackgroundColor(ContextCompat.getColor(this@WordSearchActivity, R.color.blancoOscuro))
                    }

                }
            }

        }

        newWord = ""
        selectedCells.clear()
    }

    private fun isPointInsideView(startCoord: Pair<Float, Float>, view: View, x: Float, y: Float): Boolean {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val viewX = location[0]
        val viewY = location[1]

        return (x > viewX && x < viewX + view.width && y > viewY && y < viewY + view.height)
    }


}