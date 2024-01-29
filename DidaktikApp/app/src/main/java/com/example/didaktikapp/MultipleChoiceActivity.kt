package com.example.didaktikapp

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.didaktikapp.navigation.NavigationUtil
import com.example.didaktikapp.titleFragment.TitleFragment

class MultipleChoiceActivity : AppCompatActivity() {
    private val repeatActivityMenu = RepeatActivityMenu(this)

    var myQuestion = hashMapOf<String, MultipleChoiceActivity.Question>()

    private var isPictureAnswer: Boolean? = null
    private var hasFourAnswers: Boolean? = null
    private var correctAnswer: Char? = null
    private var answers: MutableMap<Char, String?> = mutableMapOf()
    private var pictures: MutableMap<Char, Bitmap?> = mutableMapOf()
    private var chosenAnswer: Char? = null

    private lateinit var galderaText: TextView
    private lateinit var aukerak: ListView
    private lateinit var amaitu: Button


    private var gameManagerService: GameManagerService? = GameManagerService()
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        myQuestion = hashMapOf<String, MultipleChoiceActivity.Question>(
            "Juego1.3" to MultipleChoiceActivity.Question(
                resources.getString(R.string.galdera1_3), null, null, null, null, R.drawable.behiaerantzuna, R.drawable.idiak, R.drawable.zezena, resources.getString(R.string.erantzun1_3)[0]),
            "Juego1.4" to MultipleChoiceActivity.Question(
                this.resources.getString(R.string.galdera1_4_1), this.resources.getString(R.string.aukera1_4_1_a), this.resources.getString(R.string.aukera1_4_1_b), this.resources.getString(R.string.aukera1_4_1_c), null, null, null, null,  this.resources.getString(R.string.erantzun1_4_1)[0]),
            "Juego1.5" to MultipleChoiceActivity.Question(
                this.resources.getString(R.string.galdera1_4_2), this.resources.getString(R.string.aukera1_4_2_a), this.resources.getString(R.string.aukera1_4_2_b), this.resources.getString(R.string.aukera1_4_2_c), null, null, null, null,  this.resources.getString(R.string.erantzun1_4_2)[0]),
            "Juego1.6" to MultipleChoiceActivity.Question(
                this.resources.getString(R.string.galdera1_4_3), this.resources.getString(R.string.aukera1_4_3_a), this.resources.getString(R.string.aukera1_4_3_b), this.resources.getString(R.string.aukera1_4_3_c), null, null, null, null, this.resources.getString(R.string.erantzun1_4_3)[0]),
            "Juego4.3" to MultipleChoiceActivity.Question(
                this.resources.getString(R.string.galdera4_3_1), this.resources.getString(R.string.aukera4_3_1_a), this.resources.getString(R.string.aukera4_3_1_b), this.resources.getString(R.string.aukera4_3_1_c), this.resources.getString(R.string.aukera4_3_1_d), null, null, null,  this.resources.getString(R.string.erantzun4_3_1)[0]),
            "Juego4.4" to MultipleChoiceActivity.Question(
                this.resources.getString(R.string.galdera4_3_2), this.resources.getString(R.string.aukera4_3_2_a), this.resources.getString(R.string.aukera4_3_2_b), this.resources.getString(R.string.aukera4_3_2_c), this.resources.getString(R.string.aukera4_3_2_d), null, null, null, this.resources.getString(R.string.erantzun4_3_2)[0]),
            "Juego4.5" to MultipleChoiceActivity.Question(
                this.resources.getString(R.string.galdera4_3_3), this.resources.getString(R.string.aukera4_3_3_a), this.resources.getString(R.string.aukera4_3_2_b), this.resources.getString(R.string.aukera4_3_3_c), this.resources.getString(R.string.aukera4_3_3_d), null, null, null, this.resources.getString(R.string.erantzun4_3_3)[0]),
            "Juego4.6" to MultipleChoiceActivity.Question(
                this.resources.getString(R.string.galdera4_3_4), this.resources.getString(R.string.aukera4_3_4_a), this.resources.getString(R.string.aukera4_3_2_b), this.resources.getString(R.string.aukera4_3_4_c), this.resources.getString(R.string.aukera4_3_4_d), null, null, null, this.resources.getString(R.string.erantzun4_3_4)[0]),
            "Juego4.7" to MultipleChoiceActivity.Question(
                this.resources.getString(R.string.galdera4_3_5), this.resources.getString(R.string.aukera4_3_5_a), this.resources.getString(R.string.aukera4_3_2_b), this.resources.getString(R.string.aukera4_3_5_c), this.resources.getString(R.string.aukera4_3_5_d), null, null, null, this.resources.getString(R.string.erantzun4_3_5)[0]),
            "Juego4.8" to MultipleChoiceActivity.Question(
                this.resources.getString(R.string.galdera4_3_6), this.resources.getString(R.string.aukera4_3_6_a), this.resources.getString(R.string.aukera4_3_2_b), this.resources.getString(R.string.aukera4_3_6_c), this.resources.getString(R.string.aukera4_3_6_d), null, null, null, this.resources.getString(R.string.erantzun4_3_6)[0])
        )
        setContentView(R.layout.activity_multiple_choice)

        val pantalla = GameManager.get()?.pantallaActual()
        initializeVariables(pantalla)
        setupHeaderFragment(savedInstanceState)
        adaptAnswers()

        gameManagerService = GameManager.get()
        progressBar = findViewById(R.id.multipleChoiceProgressBar)
        gameManagerService!!.setInitialProgress(progressBar)

        amaitu.setOnClickListener{
            if (chosenAnswer?.equals(correctAnswer) == true){
                gameManagerService?.addProgress(progressBar)
                val intent = Intent(this ,Crucigrama::class.java)
                repeatActivityMenu.showGameOverDialog(this, intent)
            } else{
                Toast.makeText(this, "Gaizki", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun setupHeaderFragment(savedInstanceState: Bundle?) {
        val fragmentContainer = findViewById<FrameLayout>(R.id.titleFragmentTag)
        if (savedInstanceState == null) {
            val titleFragment = TitleFragment.newInstance("Aukeratu erantzun zuzena")
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

    private fun initializeVariables(pantalla:String?){
        galderaText = findViewById(R.id.galdera)
        aukerak = findViewById(R.id.aukerak)
        amaitu = findViewById(R.id.terminarMultipleChoice)

        if(myQuestion[pantalla]?.respuesta1 == null){
            isPictureAnswer = true
        } else{
            isPictureAnswer = false
            if(myQuestion[pantalla]?.respuesta4 == null){
                hasFourAnswers = false
            }else{
                hasFourAnswers = true
            }
        }
        galderaText.text = myQuestion[pantalla]?.pregunta
        correctAnswer = myQuestion[pantalla]?.answer
        answers['a'] = myQuestion[pantalla]?.respuesta1
        answers['b'] = myQuestion[pantalla]?.respuesta2
        answers['c'] = myQuestion[pantalla]?.respuesta3
        if (hasFourAnswers == true){
            answers['d'] = myQuestion[pantalla]?.respuesta4
        }

        if(isPictureAnswer == true) {

            pictures['a'] = drawableToBitmap(myQuestion[pantalla]?.picture1)
            pictures['b'] = drawableToBitmap(myQuestion[pantalla]?.picture2)
            pictures['c'] = drawableToBitmap(myQuestion[pantalla]?.picture3)
        }
    }

    private fun adaptAnswers(){
        if(isPictureAnswer == true) {
            val adapter = PictureAnswerAdapter(this, pictures, chosenAnswer, object : PictureAnswerAdapter.OnButtonClickListener {
                override fun onButtonClick(key: Char) {
                    chosenAnswer = key
                    adaptAnswers()
                }
            })
            aukerak.adapter = adapter

        }else{
            val adapter = AnswerAdapter(this, answers, chosenAnswer, object : AnswerAdapter.OnButtonClickListener {
                override fun onButtonClick(key: Char) {
                    chosenAnswer = key
                    adaptAnswers()
                }
            })
            aukerak.adapter = adapter
        }
    }

    private fun Context.drawableToBitmap(drawableId: Int?): Bitmap {
        val drawable = this.resources.getDrawable(drawableId!!, null)
        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }

        val bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        } else {
            Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        }

        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    data class Question(val pregunta: String, val respuesta1:String?, val respuesta2:String?, val respuesta3:String?, val respuesta4:String?, val picture1:Int?, val picture2:Int?, val picture3:Int?, val answer:Char)
}