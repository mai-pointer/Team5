package com.example.didaktikapp

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.didaktikapp.navigation.NavigationUtil
import com.example.didaktikapp.titleFragment.TitleFragment
import kotlin.random.Random
import kotlinx.coroutines.*

private const val SIZE = 11
private const val NUMBER_OF_WORDS = 6

class WordSearchActivity : AppCompatActivity() {

    private val repeatActivityMenu = RepeatActivityMenu(this)

    private var gameManagerService: GameManagerService? = GameManagerService()
    private lateinit var progressBar: ProgressBar

    private lateinit var tableLayout: TableLayout
    private lateinit var word1: TextView
    private lateinit var word2: TextView
    private lateinit var word3: TextView
    private lateinit var word4: TextView
    private lateinit var word5: TextView
    private lateinit var word6: TextView
    private lateinit var myWordSearch: Array<Array<Char>>

    private val wordList = listOf(
        "ODOLA",
        "KIPULA",
        "PORRUA",
        "KOIPEA",
        "ARROZA",
        "PIPERHAUTSA",
        "GATZA",
        "ODOLOSTEA",
        "TXARRIA"
    )
    private var wordMap: MutableMap<String, MutableList<Pair<Int, Int>>> = mutableMapOf()


    private val correctCells = mutableListOf<Int>()
    private var startCoordinate: Pair<Float, Float>? = null
    private var newWord: String = ""
    private var selectedCells: MutableList<Int> = mutableListOf()
    private var foundWords = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_search)

        gameManagerService = GameManager.get()
        progressBar = findViewById(R.id.wordSearchProgressBar)
        gameManagerService!!.setInitialProgress(progressBar)

        initializeViews()
        selectWords()
        setupHeaderFragment(savedInstanceState)

        createGame()

        findViewById<Button>(R.id.terminar_sopaDeLetras).setOnClickListener{
            if (foundWords >= wordMap.size){
                gameManagerService?.addProgress(progressBar)
                val intent = Intent(this ,Crucigrama::class.java)
                repeatActivityMenu.showGameOverDialog(this, intent)
            }

        }
    }

    fun createGame() = lifecycleScope.launch {
        progressBar.visibility = View.VISIBLE
        //delay(5000L)
        val wordSearchJob = async(Dispatchers.IO) {
            createWordSearch()
        }
        wordSearchJob.await()
        progressBar.visibility = View.GONE
        createGrid(tableLayout)
    }

    private fun initializeViews() {
        progressBar = findViewById(R.id.progressBar)
        tableLayout = findViewById(R.id.wordSearch)
        word1 = findViewById(R.id.word1)
        word2 = findViewById(R.id.word2)
        word3 = findViewById(R.id.word3)
        word4 = findViewById(R.id.word4)
        word5 = findViewById(R.id.word5)
        word6 = findViewById(R.id.word6)
    }

    private fun setupHeaderFragment(savedInstanceState: Bundle?) {
        val fragmentContainer = findViewById<FrameLayout>(R.id.titleFragmentTag)
        if (savedInstanceState == null) {
            val titleFragment = TitleFragment.newInstance("Letra zopa")
            supportFragmentManager.beginTransaction()
                .replace(fragmentContainer.id, titleFragment, "titleFragmentTag")
                .commit()
        }
        val titleFragment =
            supportFragmentManager.findFragmentByTag("titleFragmentTag") as TitleFragment?
        titleFragment?.setOnHomeButtonClickListener {
            onHomeButtonClicked()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        createGrid(tableLayout)
    }

    private fun selectWords() {
        val randomWords = wordList.shuffled().take(NUMBER_OF_WORDS)

        val wordTextViews = listOf(word1, word2, word3, word4, word5, word6)

        randomWords.forEachIndexed { index, word ->
            wordTextViews[index].text = word
        }
        wordMap.putAll(randomWords.associateWith { mutableListOf<Pair<Int, Int>>() })
        myWordSearch = Array(SIZE) { Array(SIZE) { ' ' } }
    }

    private fun createWordSearch() {
        var wordWritten: Boolean
        setIsolatedWord(wordMap.keys.elementAt(0))
        wordMap.keys.forEachIndexed { index, element ->
            if (index > 0) {
                wordWritten = false
                var wordToCompareIndex = index - 1

                while (wordToCompareIndex >= 0) {
                    val commonCharPos =
                        hasCommonChar(element, wordMap.keys.elementAt(wordToCompareIndex))

                    if (commonCharPos != null) {
                        val firstCommonPos = commonCharPos.first
                        val secondCommonPos = commonCharPos.second
                        val coordinates: Pair<Int, Int> =
                            wordMap[wordMap.keys.elementAt(wordToCompareIndex)]!![secondCommonPos]
                        var randomDirection = Random.nextInt(enumValues<Direction>().size)
                        var attempts = 0
                        val maxAttempts = enumValues<Direction>().size

                        while (!canWriteWord(
                                coordinates.first,
                                coordinates.second,
                                element,
                                Direction.values()[randomDirection],
                                firstCommonPos
                            ) && attempts < maxAttempts
                        ) {
                            randomDirection = (randomDirection + 1) % enumValues<Direction>().size
                            attempts++
                        }

                        if (attempts < maxAttempts) {
                            writeWord(
                                coordinates.first,
                                coordinates.second,
                                element,
                                Direction.values()[randomDirection],
                                firstCommonPos
                            )
                            wordWritten = true
                            break
                        }
                    }
                    wordToCompareIndex--
                }
                if (!wordWritten) {
                    setIsolatedWord(wordMap.keys.elementAt(index))
                }
            }
        }
    }

    private fun hasCommonChar(myCurrentWord: String, previousWord: String): Pair<Int, Int>? {
        for ((indexChar, char) in myCurrentWord.withIndex()) {
            for ((indexPreChar, preChar) in previousWord.withIndex()) {
                if (char == preChar) {
                    return Pair(indexChar, indexPreChar)
                }
            }
        }
        return null
    }

    private fun reset() {
        for (row in 0 until SIZE) {
            for (col in 0 until SIZE) {
                myWordSearch[row][col] = ' '
            }
        }
        createWordSearch()
    }

    private fun setIsolatedWord(word: String) {
        val maxAttempts = Direction.values().size
        var attempts = 0
        val maxAttempts1 = SIZE * SIZE
        var attempts1 = 0

        do {
            val randomRow = Random.nextInt(SIZE)
            val randomCol = Random.nextInt(SIZE)
            var randomDirection = Random.nextInt(Direction.values().size)

            while (!canWriteWord(
                    randomRow,
                    randomCol,
                    word,
                    Direction.values()[randomDirection],
                    0
                ) && attempts < maxAttempts
            ) {
                randomDirection = (randomDirection + 1) % enumValues<Direction>().size
                attempts++
            }

            if (attempts < maxAttempts) {
                writeWord(randomRow, randomCol, word, Direction.values()[randomDirection], 0)
                break
            }

            attempts1++
        } while (attempts1 < maxAttempts1)

        if (attempts1 == maxAttempts1) {
            reset()
        }
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

    private fun canWriteWord(
        startRow: Int,
        startCol: Int,
        word: String,
        direction: Direction,
        startIndex: Int
    ): Boolean {
        val endIndex = (word.length - 1) - startIndex

        when (direction) {
            Direction.HORIZONTAL -> if (startCol + endIndex >= SIZE || startCol - startIndex < 0) return false
            Direction.REVERSE_HORIZONTAL -> if (startCol - endIndex < 0 || startCol + startIndex >= SIZE) return false
            Direction.VERTICAL_DOWN -> if (startRow + endIndex >= SIZE || startRow - startIndex < 0) return false
            Direction.VERTICAL_UP -> if (startRow - endIndex < 0 || startRow + startIndex >= SIZE) return false
            Direction.DIAGONAL_UP_RIGHT -> if (startRow - endIndex < 0 || startCol + endIndex >= SIZE || startCol - startIndex < 0 || startRow + startIndex >= SIZE) return false
            Direction.DIAGONAL_UP_LEFT -> if (startRow - endIndex < 0 || startCol - endIndex < 0 || startCol + startIndex >= SIZE || startRow + startIndex >= SIZE) return false
            Direction.DIAGONAL_DOWN_RIGHT -> if (startRow + endIndex >= SIZE || startCol + endIndex >= SIZE || startCol - startIndex < 0 || startRow - startIndex < 0) return false
            Direction.DIAGONAL_DOWN_LEFT -> if (startRow + endIndex >= SIZE || startCol - endIndex < 0 || startCol + startIndex >= SIZE || startRow - startIndex < 0) return false
        }
        var posIndex = 0
        for (i in startIndex until word.length) {
            when (direction) {
                Direction.HORIZONTAL -> {
                    val cellValue = myWordSearch[startRow][startCol + posIndex]
                    if (cellValue != ' ' && cellValue != word[i]) {
                        return false
                    }
                }

                Direction.REVERSE_HORIZONTAL -> {
                    val cellValue = myWordSearch[startRow][startCol - posIndex]
                    if (cellValue != ' ' && cellValue != word[i]) {
                        return false
                    }
                }

                Direction.VERTICAL_DOWN -> {
                    val cellValue = myWordSearch[startRow + posIndex][startCol]
                    if (cellValue != ' ' && cellValue != word[i]) {
                        return false
                    }
                }

                Direction.VERTICAL_UP -> {
                    val cellValue = myWordSearch[startRow - posIndex][startCol]
                    if (cellValue != ' ' && cellValue != word[i]) {
                        return false
                    }
                }

                Direction.DIAGONAL_UP_RIGHT -> {
                    val cellValue = myWordSearch[startRow - posIndex][startCol + posIndex]
                    if (cellValue != ' ' && cellValue != word[i]) {
                        return false
                    }
                }

                Direction.DIAGONAL_UP_LEFT -> {
                    val cellValue = myWordSearch[startRow - posIndex][startCol - posIndex]
                    if (cellValue != ' ' && cellValue != word[i]) {
                        return false
                    }
                }

                Direction.DIAGONAL_DOWN_RIGHT -> {
                    val cellValue = myWordSearch[startRow + posIndex][startCol + posIndex]
                    if (cellValue != ' ' && cellValue != word[i]) {
                        return false
                    }
                }

                Direction.DIAGONAL_DOWN_LEFT -> {
                    val cellValue = myWordSearch[startRow + posIndex][startCol - posIndex]
                    if (cellValue != ' ' && cellValue != word[i]) {
                        return false
                    }
                }
            }
            posIndex += 1
        }
        if (startIndex > 0) {
            posIndex = 0
            for (i in startIndex downTo 0) {
                when (direction) {
                    Direction.HORIZONTAL -> {
                        val cellValue = myWordSearch[startRow][startCol - posIndex]
                        if (cellValue != ' ' && cellValue != word[i]) {
                            return false
                        }
                    }

                    Direction.REVERSE_HORIZONTAL -> {
                        val cellValue = myWordSearch[startRow][startCol + posIndex]
                        if (cellValue != ' ' && cellValue != word[i]) {
                            return false
                        }
                    }

                    Direction.VERTICAL_DOWN -> {
                        val cellValue = myWordSearch[startRow - posIndex][startCol]
                        if (cellValue != ' ' && cellValue != word[i]) {
                            return false
                        }
                    }

                    Direction.VERTICAL_UP -> {
                        val cellValue = myWordSearch[startRow + posIndex][startCol]
                        if (cellValue != ' ' && cellValue != word[i]) {
                            return false
                        }
                    }

                    Direction.DIAGONAL_UP_RIGHT -> {
                        val cellValue = myWordSearch[startRow + posIndex][startCol - posIndex]
                        if (cellValue != ' ' && cellValue != word[i]) {
                            return false
                        }
                    }

                    Direction.DIAGONAL_UP_LEFT -> {
                        val cellValue = myWordSearch[startRow + posIndex][startCol + posIndex]
                        if (cellValue != ' ' && cellValue != word[i]) {
                            return false
                        }
                    }

                    Direction.DIAGONAL_DOWN_RIGHT -> {
                        val cellValue = myWordSearch[startRow - posIndex][startCol - posIndex]
                        if (cellValue != ' ' && cellValue != word[i]) {
                            return false
                        }
                    }

                    Direction.DIAGONAL_DOWN_LEFT -> {
                        val cellValue = myWordSearch[startRow - posIndex][startCol + posIndex]
                        if (cellValue != ' ' && cellValue != word[i]) {
                            return false
                        }
                    }
                }
                posIndex += 1
            }
        }

        return true
    }

    private fun writeWord(
        startRow: Int,
        startCol: Int,
        word: String,
        direction: Direction,
        startFromIndex: Int
    ) {

        var posIndex = 0
        for (i in startFromIndex until word.length) {
            when (direction) {
                Direction.HORIZONTAL -> writeCell(startRow, startCol + posIndex, word[i], word)
                Direction.REVERSE_HORIZONTAL -> writeCell(
                    startRow,
                    startCol - posIndex,
                    word[i],
                    word
                )

                Direction.VERTICAL_DOWN -> writeCell(startRow + posIndex, startCol, word[i], word)
                Direction.VERTICAL_UP -> writeCell(startRow - posIndex, startCol, word[i], word)
                Direction.DIAGONAL_UP_RIGHT -> writeCell(
                    startRow - posIndex,
                    startCol + posIndex,
                    word[i],
                    word
                )

                Direction.DIAGONAL_UP_LEFT -> writeCell(
                    startRow - posIndex,
                    startCol - posIndex,
                    word[i],
                    word
                )

                Direction.DIAGONAL_DOWN_RIGHT -> writeCell(
                    startRow + posIndex,
                    startCol + posIndex,
                    word[i],
                    word
                )

                Direction.DIAGONAL_DOWN_LEFT -> writeCell(
                    startRow + posIndex,
                    startCol - posIndex,
                    word[i],
                    word
                )
            }
            posIndex += 1
        }

        if (startFromIndex > 0) {
            posIndex = 0
            for (i in startFromIndex downTo 0) {
                when (direction) {
                    Direction.HORIZONTAL -> writeCell(startRow, startCol - posIndex, word[i], word)
                    Direction.REVERSE_HORIZONTAL -> writeCell(
                        startRow,
                        startCol + posIndex,
                        word[i],
                        word
                    )

                    Direction.VERTICAL_DOWN -> writeCell(
                        startRow - posIndex,
                        startCol,
                        word[i],
                        word
                    )

                    Direction.VERTICAL_UP -> writeCell(startRow + posIndex, startCol, word[i], word)
                    Direction.DIAGONAL_UP_RIGHT -> writeCell(
                        startRow + posIndex,
                        startCol - posIndex,
                        word[i],
                        word
                    )

                    Direction.DIAGONAL_UP_LEFT -> writeCell(
                        startRow + posIndex,
                        startCol + posIndex,
                        word[i],
                        word
                    )

                    Direction.DIAGONAL_DOWN_RIGHT -> writeCell(
                        startRow - posIndex,
                        startCol - posIndex,
                        word[i],
                        word
                    )

                    Direction.DIAGONAL_DOWN_LEFT -> writeCell(
                        startRow - posIndex,
                        startCol + posIndex,
                        word[i],
                        word
                    )
                }
                posIndex += 1
            }
        }
        //wordMap[word]?.addAll(coordinates)
    }

    private fun writeCell(row: Int, col: Int, value: Char, word: String) {
        myWordSearch[row][col] = value
        wordMap[word]?.add(Pair(row, col))
    }


    private fun createGrid(tableLayout: TableLayout) {
        val allowedChars = ('A'..'Z')
        tableLayout.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    tableLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    val tableLayoutWidth = tableLayout.width
                    val tableLayoutHeight = tableLayout.height
                    val cellSize: Int
                    if (tableLayoutHeight < tableLayoutWidth) {
                        cellSize = tableLayoutHeight.div(SIZE)
                    } else {
                        cellSize = tableLayoutWidth.div(SIZE)
                    }

                    for (i in 0 until SIZE) {
                        // Crea una nueva fila
                        val tableRow = TableRow(this@WordSearchActivity)

                        for (j in 0 until SIZE) {
                            val frameLayout = FrameLayout(this@WordSearchActivity)

                            val layoutParams = TableRow.LayoutParams(cellSize, cellSize)
                            frameLayout.layoutParams = layoutParams
                            frameLayout.setPadding(13, 13, 13, 13)
                            frameLayout.setBackgroundColor(
                                ContextCompat.getColor(
                                    this@WordSearchActivity,
                                    R.color.blancoOscuro
                                )
                            )

                            // Crear un TextView dentro del FrameLayout
                            val textView = TextView(this@WordSearchActivity)

                            val textLayoutParams = FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT
                            )
                            textView.layoutParams = textLayoutParams
                            textView.gravity = Gravity.CENTER
                            textView.textSize = 14f
                            if (myWordSearch[i][j].toString() != " ") {
                                textView.text = myWordSearch[i][j].toString()
                                Log.d("sopa", myWordSearch[i][j].toString())
                            } else {
                                textView.text = allowedChars.random().toString()
                            }
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
                                if (!selectedCells.contains(textView.id)) {
                                    newWord += textView.text
                                    selectedCells.add(textView.id)
                                    frameLayout.setBackgroundColor(
                                        ContextCompat.getColor(
                                            this@WordSearchActivity,
                                            R.color.naranja
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
                return true
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                startCoordinate = null
                handleClick()
                return true
            }

            else -> return false
        }
    }

    private fun handleClick() {
        val toast = Toast.makeText(this, newWord, Toast.LENGTH_LONG)
        toast.show()

        for (i in 0 until tableLayout.childCount) {
            val row = tableLayout.getChildAt(i) as TableRow
            for (j in 0 until row.childCount) {
                val frameLayout = row.getChildAt(j) as FrameLayout
                val textView = frameLayout.getChildAt(0) as TextView

                selectedCells.forEach { code ->
                    if (code == textView.id) {
                        if (wordMap.keys.contains(newWord)) {
                            frameLayout.setBackgroundColor(
                                ContextCompat.getColor(
                                    this@WordSearchActivity,
                                    R.color.verdeClaro
                                )
                            )
                            correctCells.add(textView.id)
                            foundWords += 1
                        } else {
                            if (correctCells.contains(textView.id)) {
                                frameLayout.setBackgroundColor(
                                    ContextCompat.getColor(
                                        this@WordSearchActivity,
                                        R.color.verdeClaro
                                    )
                                )
                            } else {
                                frameLayout.setBackgroundColor(
                                    ContextCompat.getColor(
                                        this@WordSearchActivity,
                                        R.color.blancoOscuro
                                    )
                                )
                            }
                        }

                    }

                }
            }
        }

        if (wordMap.keys.contains(newWord)) {
            if (word1.text == newWord) {
                word1.paintFlags = word1.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else if (word2.text == newWord) {
                word2.paintFlags = word1.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else if (word3.text == newWord) {
                word3.paintFlags = word1.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else if (word4.text == newWord) {
                word4.paintFlags = word1.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else if (word5.text == newWord) {
                word5.paintFlags = word1.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                word6.paintFlags = word1.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
        }
        newWord = ""
        selectedCells.clear()
    }

    private fun isPointInsideView(
        startCoord: Pair<Float, Float>,
        view: View,
        x: Float,
        y: Float
    ): Boolean {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val viewX = location[0]
        val viewY = location[1]

        return (x > viewX && x < viewX + view.width && y > viewY && y < viewY + view.height)
    }

    private fun onHomeButtonClicked() {
        // Acciones a realizar cuando se hace clic en el botón Home
        // Utiliza NavigationUtil para la navegación
        NavigationUtil.navigateToMainMenu(this)
    }
}