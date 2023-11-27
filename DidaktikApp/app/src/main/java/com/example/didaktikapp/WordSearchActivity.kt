package com.example.didaktikapp

import android.os.Bundle
import android.view.Gravity
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class WordSearchActivity: AppCompatActivity() {
    private lateinit var tableLayout: TableLayout
    private lateinit var myWordSearch: Array<Array<Char>>

    val size = 8

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_search)

        tableLayout = findViewById(R.id.wordSearch)

        CreateGrid(tableLayout)
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
                textView.text="A"
                textView.setBackgroundColor(resources.getColor(R.color.blancoOscuro, null))
                val uniqueId = "R:$i C:$j"
                textView.id = uniqueId.hashCode()

                textView.setOnClickListener{
                    SelectChar(textView)
                }

                tableRow.addView(textView)
            }
            tableLayout.addView(tableRow)
        }
    }

    fun SelectChar(textView: TextView){
        if (textView.backgroundTintList?.defaultColor == ContextCompat.getColor(this, R.color.blancoOscuro)) {
            textView.setBackgroundColor(ContextCompat.getColor(this, R.color.verdeClaro))
        }
    }

}