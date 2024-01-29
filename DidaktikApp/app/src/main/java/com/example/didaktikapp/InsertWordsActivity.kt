package com.example.didaktikapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.didaktikapp.navigation.NavigationUtil
import com.example.didaktikapp.titleFragment.TitleFragment

class InsertWordsActivity : AppCompatActivity() {

    private val originalText =
        "Dorre hau (gotorleku militarra / erregearen etxea / eliza) izan zen eta (XVI. / XVIII. / XX.) mendean eraiki zen. (Adreiluz / Granitoz / Hareharriz) eginda dago eta hiru solairu ditu. Gaur egun, dorrea (udaletxe / etxebizitza / museo) bihurtu da."

    private val selectedOptions = mutableSetOf<String>()
    private val repeatActivityMenu = RepeatActivityMenu(this)
    private var gameManagerService: GameManagerService? = GameManagerService()
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert_words)

        gameManagerService = GameManager.get()
        progressBar = findViewById(R.id.ordenarImagenesProgressBar)
        gameManagerService!!.setInitialProgress(progressBar)

        // Reemplaza el contenedor con el TitleFragment
        val fragmentContainer = findViewById<FrameLayout>(R.id.titleFragmentTag)

        setupHeaderFragment(savedInstanceState)

        val textView = findViewById<TextView>(R.id.tv1)
        textView.text = createSpannableText(originalText)
        textView.movementMethod = LinkMovementMethod.getInstance()

        val btnValidate = findViewById<Button>(R.id.terminar_insertWords)
        btnValidate.setOnClickListener {
            validateText()
        }
    }

    private fun setupHeaderFragment(savedInstanceState: Bundle?) {
        val fragmentContainer = findViewById<FrameLayout>(R.id.titleFragmentTag)
        if (savedInstanceState == null) {
            val titleFragment = TitleFragment.newInstance(resources.getString(R.string.insertWordsTitle))
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

    private fun onHomeButtonClicked() {
        NavigationUtil.navigateToMainMenu(this)
    }

    private fun createSpannableText(text: String): SpannableStringBuilder {
        val spannableStringBuilder = SpannableStringBuilder(text)

        val regex = Regex("\\((.*?)\\)")
        val matches = regex.findAll(text)

        matches.forEach { matchResult ->
            val start = matchResult.range.first
            val end = matchResult.range.last

            spannableStringBuilder.setSpan(
                ForegroundColorSpan(Color.BLUE), start, end + 1,
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            val optionsText = matchResult.groupValues[1]
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    handleSelection(optionsText, start, end)
                }
            }

            spannableStringBuilder.setSpan(clickableSpan, start, end + 1, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return spannableStringBuilder
    }

    private fun handleSelection(optionsText: String, start: Int, end: Int) {
        if (!selectedOptions.contains(optionsText)) {
            val options = optionsText.split(" / ")

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Selecciona una opción")
                .setItems(options.toTypedArray()) { dialog, which ->
                    val selectedOption = options[which]
                    val newText = (findViewById<TextView>(R.id.tv1)).text.toString()
                        .replaceRange(start + 1, end, selectedOption)
                    (findViewById<TextView>(R.id.tv1)).text = createSpannableText(newText)
                    selectedOptions.add(optionsText)
                    dialog.dismiss()
                }

            val dialog = builder.create()
            dialog.show()
        }
    }
    private fun validateText() {
        val insertedText = findViewById<TextView>(R.id.tv1).text.toString()
        val expectedText = "Dorre hau (gotorleku militarra) izan zen eta (XVI.) mendean eraiki zen. (Hareharriz) eginda dago eta hiru solairu ditu. Gaur egun, dorrea (etxebizitza) bihurtu da."

        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Resultado de la validación")
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        if (insertedText == expectedText) {
            gameManagerService?.addProgress(progressBar)
            val intent = Intent(this ,InsertWordsActivity::class.java)
            repeatActivityMenu.showGameOverDialog(this, intent)
        } else {
            alertDialog.setMessage("Perdiste")
            alertDialog.show()

            Reiniciar()
        }

    }

    private fun Reiniciar() {
        val intent = Intent(this, InsertWordsActivity::class.java)
        startActivity(intent)
    }
}