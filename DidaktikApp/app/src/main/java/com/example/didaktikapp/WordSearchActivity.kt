package com.example.didaktikapp

import android.os.Bundle
import android.view.Gravity
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class WordSearchActivity: AppCompatActivity() {
    private lateinit var TableLayout: TableLayout
    private lateinit var myWordSearch: Array<Array<Char>>

    val size = 8

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_search)

        TableLayout = findViewById(R.id.wordSearch)

        myWordSearch = Array(size) { Array(size) { ' ' } }

    }

    fun CreateGrid(tableLayout:TableLayout) {

        for (i in 0 until size) {
            // Crea una nueva fila
            val tableRow = TableRow(this)

            for (j in 0 until size) {
                val textView = TextView(this)
                textView.layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT,
                    1.0f
                )
                textView.gravity = Gravity.CENTER

                val uniqueId = "R:$i C:$j"
                textView.id = uniqueId.hashCode()

                tableRow.addView(textView)
            }
            tableLayout.addView(tableRow)
        }
    }

}