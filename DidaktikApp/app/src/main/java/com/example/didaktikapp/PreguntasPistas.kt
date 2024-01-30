package com.example.didaktikapp

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.didaktikapp.navigation.NavigationUtil
import com.example.didaktikapp.titleFragment.TitleFragment

class PreguntasPistas : AppCompatActivity() {
    private val repeatActivityMenu = RepeatActivityMenu(this)

    var myPista = hashMapOf<String, PreguntasPistas.Pista>()
    var mediaPlayer: MediaPlayer? = null
    private var hasPictures: Boolean? = null
    private var hasAudio: Boolean? = null
    private var correctAnswer1: String? = null
    private var correctAnswer2: String? = null
    private var pictures: MutableList<Bitmap> = mutableListOf()

    private lateinit var userAnswer: EditText
    private lateinit var galderaText: TextView
    private lateinit var pistaText: TextView
    private lateinit var pistak: ImageView
    private lateinit var baieztatu: Button
    private lateinit var playaudio: ImageButton

    private var gameManagerService: GameManagerService? = GameManagerService()
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preguntas_pistas)

        val pantalla = GameManager.get()?.pantallaActual()

        myPista = hashMapOf<String, PreguntasPistas.Pista>(
            "Juego1.1" to PreguntasPistas.Pista(
                resources.getString(R.string.galdera1_1), R.drawable.idiprobakpista,  R.raw.idi, resources.getString(R.string.pista1_1), resources.getString(R.string.erantzun1_1a), resources.getString(R.string.erantzun1_1b)),
            "Juego2.1" to PreguntasPistas.Pista(
                resources.getString(R.string.galdera2_1), R.drawable.odolostepista, null, null, resources.getString(R.string.erantzun2_1a), resources.getString(R.string.erantzun2_1b)),
            "Juego3.1" to PreguntasPistas.Pista(
                resources.getString(R.string.galdera3_1), R.drawable.txakolipista, null,null, resources.getString(R.string.erantzun3_1a), resources.getString(R.string.erantzun3_1b)),
            "Juego5.2" to PreguntasPistas.Pista(
                resources.getString(R.string.galdera5_2), null, null, null, resources.getString(R.string.erantzun5_2a), resources.getString(R.string.erantzun5_2b))
        )

        gameManagerService = GameManager.get()
        progressBar = findViewById(R.id.pistaProgressBar)
        gameManagerService!!.setInitialProgress(progressBar)

        initializeVariables(pantalla)
        setupHeaderFragment(savedInstanceState)

        playaudio.setOnClickListener{
            mediaPlayer?.start()
            playaudio.setColorFilter(resources.getColor(R.color.verdeOscuro), PorterDuff.Mode.SRC_IN)
        }

        baieztatu.setOnClickListener{
            val answer = userAnswer.text.toString().uppercase()
            if (answer == correctAnswer1 || answer == correctAnswer2){
                gameManagerService?.addProgress(progressBar)
                val intent = Intent(this, PreguntasPistas::class.java)
                repeatActivityMenu.showGameOverDialog(this, intent)
            } else{
                Toast.makeText(this, "Gaizki", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupHeaderFragment(savedInstanceState: Bundle?) {
        val fragmentContainer = findViewById<FrameLayout>(R.id.fragmentContainerView)
        if (savedInstanceState == null) {
            val titleFragment = TitleFragment.newInstance(resources.getString(R.string.pistakTitle))
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
        userAnswer = findViewById(R.id.userAnswer)
        galderaText = findViewById(R.id.galdera)
        pistaText = findViewById(R.id.pistaText)
        pistak = findViewById(R.id.imagepista)
        playaudio = findViewById(R.id.playaudio)
        baieztatu = findViewById(R.id.terminar_preguntasPistas)

        correctAnswer1 = myPista[pantalla]?.answer!!
        correctAnswer2 = myPista[pantalla]?.answer2!!

        if(myPista[pantalla]?.picture1 != null) {
            pistak.visibility = View.VISIBLE
            pistak.setImageBitmap(drawableToBitmap(myPista[pantalla]?.picture1))
        } else{
            pistak.visibility = View.INVISIBLE
        }

        if(myPista[pantalla]?.audio != null){
            playaudio.visibility = View.VISIBLE
            mediaPlayer = MediaPlayer.create(this, myPista[pantalla]?.audio!!)
        } else{
            playaudio.visibility = View.INVISIBLE
        }

        if(myPista[pantalla]?.pista != null){
            pistaText.visibility = View.VISIBLE
            pistaText.text = myPista[pantalla]?.pista
        } else{
            pistaText.visibility = View.INVISIBLE
        }
        galderaText.text = myPista[pantalla]?.pregunta
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

    data class Pista(val pregunta: String, val picture1:Int?, val audio: Int?, val pista: String?, val answer:String, val answer2:String)
}


