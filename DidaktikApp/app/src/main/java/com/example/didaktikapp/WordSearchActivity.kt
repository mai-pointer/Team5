package com.example.didaktikapp

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*

class WordSearchActivity: AppCompatActivity() {
    private lateinit var tableLayout: TableLayout
    private lateinit var myWordSearch: Array<Array<Char>>

    val size = 8

    private var startCoordinate: Pair<Float, Float>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_search)

        tableLayout = findViewById(R.id.wordSearch)
        val layoutParams = tableLayout.layoutParams
        layoutParams.height = layoutParams.width
        tableLayout.layoutParams = layoutParams

        CreateGrid(tableLayout)
        myWordSearch = Array(size) { Array(size) { ' ' } }

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
                            val textView = TextView(this@WordSearchActivity)

                            val layoutParams = TableRow.LayoutParams(cellSize, cellSize)
                            textView.layoutParams = layoutParams

                            textView.gravity = Gravity.CENTER
                            textView.text = "A"
                            textView.setBackgroundColor(
                                resources.getColor(R.color.blancoOscuro, null)
                            )
                            val uniqueId = "R:$i C:$j"
                            textView.id = uniqueId.hashCode()

                            textView.setOnTouchListener { _, event ->
                                onTouchEvent(event)
                            }

                            tableRow.addView(textView)
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
                            val textView = row.getChildAt(j) as TextView
                            if (isPointInsideView(startCoord, textView, event.rawX, event.rawY)) {
                                textView.setBackgroundColor(ContextCompat.getColor(this@WordSearchActivity, R.color.verdeClaro))
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
        // Implementa aquí la lógica de manejo de clics
        // Por ejemplo, puedes realizar las acciones que necesitas al hacer clic
    }


    private fun isPointInsideView(startCoord: Pair<Float, Float>, view: View, x: Float, y: Float): Boolean {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val viewX = location[0]
        val viewY = location[1]

        return (x > viewX && x < viewX + view.width && y > viewY && y < viewY + view.height)
    }


}