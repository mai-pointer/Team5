package com.example.didaktikapp

import android.os.Bundle
import android.view.Gravity
import android.view.ViewTreeObserver
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class InsertWordsActivity : AppCompatActivity() {

    private lateinit var tableLayout: TableLayout
    private var textua =
        "Dorre hau (gotorleku militarra / erregearen etxea / eliza) izan zen eta (XVI. / XVIII. / XX.) mendean eraiki zen. (Adreiluz / Granitoz / Hareharriz) eginda dago eta hiru solairu ditu. Gaur egun, dorrea (udaletxe / etxebizitza / museo) bihurtu da."

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert_words)

        tableLayout = findViewById<TableLayout>(R.id.tableLayout)

        tableLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                tableLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                insertText(tableLayout)
            }
        })
    }

    private fun insertText(tableLayout:TableLayout) {
        var tableRow: TableRow? = null
        val layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )
        textua.split("\\s+".toRegex()).forEach { hitza: String ->
            val textView = TextView(this@InsertWordsActivity)

            textView.gravity = Gravity.CENTER
            textView.text = hitza
            textView.setBackgroundColor(resources.getColor(R.color.blancoOscuro, null))
            textView.layoutParams = layoutParams

            textView.measure(0, 0)
            layoutParams.width = textView.measuredWidth

            if (tableRow == null || tableRow!!.measuredWidth + textView.measuredWidth > tableLayout.width) {
                tableRow = TableRow(this@InsertWordsActivity)
                tableLayout.addView(tableRow)
            }

            tableRow?.addView(textView)
        }
    }
}


