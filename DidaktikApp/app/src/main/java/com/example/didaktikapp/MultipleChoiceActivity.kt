package com.example.didaktikapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.didaktikapp.navigation.NavigationUtil
import com.example.didaktikapp.titleFragment.TitleFragment

class MultipleChoiceActivity : AppCompatActivity() {
    private val repeatActivityMenu = RepeatActivityMenu(this)

    val myQuestion = hashMapOf<String, MultipleChoiceActivity.Question>(
        "Juego1.3" to MultipleChoiceActivity.Question(
            "Zein da idia?", null, null, null, drawableToBitmap(R.drawable.default_image), drawableToBitmap(R.drawable.default_image), drawableToBitmap(R.drawable.default_image), 'a'),
        "Juego1.4" to MultipleChoiceActivity.Question(
            "Urteko zein unetan egiten dira idi-probak?", "Herriko festetan", "Gabonetan", "Aste Santuan", null, null, null, 'a'),
        "Juego4.3" to MultipleChoiceActivity.Question(
            "Nor zen Mikel Zarate?", "Euskal Idazlea", "Euskal bertsozalea", "Bertsolari famatua", null, null, null, 'a'))

    private var isPictureAnswer: Boolean? = null
    private var question: String? = null
    private var correctAnswer: Char? = null
    private var answers: MutableMap<Char, String?> = mutableMapOf()
    private var pictures: MutableMap<Char, Bitmap?> = mutableMapOf()
    private var chosenAnswer: Char? = null

    private lateinit var galderaText: TextView
    private lateinit var aukerak: RecyclerView
    private lateinit var baieztatu: Button
    private lateinit var atzera: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiple_choice)

        val pantalla = GameManager.get()?.pantallaActual()
        initializeVariables(pantalla)
        setupHeaderFragment(savedInstanceState)
        adaptAnswers()

        baieztatu.setOnClickListener{
            if (chosenAnswer?.equals(correctAnswer) == true){
                Toast.makeText(this, "Zorionak", Toast.LENGTH_SHORT).show()
            } else{
                Toast.makeText(this, "Gaizki", Toast.LENGTH_SHORT).show()
            }
        }
        atzera.setOnClickListener{

        }
    }
    private fun setupHeaderFragment(savedInstanceState: Bundle?) {
        val fragmentContainer = findViewById<FrameLayout>(R.id.fragmentContainerView)
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
        baieztatu = findViewById(R.id.btnValidate)
        atzera = findViewById(R.id.btnBack)

        isPictureAnswer = myQuestion[pantalla]?.respuesta1 != null

        question = myQuestion[pantalla]?.pregunta
        correctAnswer = myQuestion[pantalla]?.answer
        answers['a'] = myQuestion[pantalla]?.respuesta1
        answers['b'] = myQuestion[pantalla]?.respuesta2
        answers['c'] = myQuestion[pantalla]?.respuesta3

        pictures['a'] = myQuestion[pantalla]?.picture1
        pictures['b'] = myQuestion[pantalla]?.picture2
        pictures['c'] = myQuestion[pantalla]?.picture3
    }

    private fun adaptAnswers(){
        if(isPictureAnswer == true) {
            val adapter = PictureAnswerAdapter(this, pictures, object : PictureAnswerAdapter.OnButtonClickListener {
                override fun onButtonClick(key: Char) {
                    chosenAnswer = key
                }
            })
            aukerak.adapter = adapter as RecyclerView.Adapter<RecyclerView.ViewHolder>

        }else{
            val adapter = AnswerAdapter(this, answers, object : AnswerAdapter.OnButtonClickListener {
                override fun onButtonClick(key: Char) {
                    chosenAnswer = key
                }
            })
            aukerak.adapter = adapter as RecyclerView.Adapter<RecyclerView.ViewHolder>
        }
    }

    fun Context.drawableToBitmap(drawableId: Int): Bitmap {
        val drawable = this.resources.getDrawable(drawableId, null)
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

    data class Question(val pregunta: String, val respuesta1:String?, val respuesta2:String?, val respuesta3:String?, val picture1:Bitmap?, val picture2:Bitmap?, val picture3:Bitmap?, val answer:Char)
}