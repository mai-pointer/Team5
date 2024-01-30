package com.example.didaktikapp

import android.graphics.PorterDuff
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ProgressBar
import com.example.didaktikapp.navigation.NavigationUtil
import com.example.didaktikapp.titleFragment.TitleFragment

class Info : AppCompatActivity() {

    val informacion = hashMapOf<String, Informacion>(
        "Juego1_2" to Informacion(
            getString(R.string.Juego1_2),
            R.raw.juegounodos
        ),
        "Juego2_2" to Informacion(
            getString(R.string.Juego2_2),
            R.raw.juegodosuno
        ),
        "Juego2_4" to Informacion(
            getString(R.string.Juego2_4),
            R.raw.juegodoscuatro
        ),
        "Juego3_2" to Informacion(
            getString(R.string.Juego3_2),
            R.raw.juegotresdos
        ),
        "Juego3_3" to Informacion(
            getString(R.string.Juego3_3),
            R.raw.juegotrestres
        ),
        "Juego3_5" to Informacion(
            getString(R.string.Juego3_5),
            R.raw.juegotrescinco
        ),
        "Juego4_1" to Informacion(
            getString(R.string.Juego4_1),
            R.raw.juegocuatrouno
        ),
        "Juego4_4" to Informacion(
            getString(R.string.Juego4_4),
            R.raw.juegocuatrocuatro
        ),
        "Juego5_1" to Informacion(
            getString(R.string.Juego5_1),
            R.raw.juegocincouno
        ),
        "Juego5_3" to Informacion(
            getString(R.string.Juego5_3),
            R.raw.juegocincotres
        ),
        "Juego5_6" to Informacion(
            getString(R.string.Juego5_6),
            R.raw.juegocincoseis
        ),
        "Juego6_1" to Informacion(
            getString(R.string.Juego6_1),
            R.raw.juegoseisuno
        ),
        "Juego6_3" to Informacion(
            getString(R.string.Juego6_3),
            R.raw.juegoseistres
        ),
        "Juego7_1" to Informacion(
            getString(R.string.Juego7_1),
            R.raw.juegosieteuno
        ),
        "HASIERAKO_JARDUERA_1" to Informacion(
            getString(R.string.HASIERAKO_JARDUERA_1),
            R.raw.hjuno
        ),
        "AMAIERAKO_JARDUERA_1" to Informacion(
            getString(R.string.AMAIERAKO_JARDUERA_1),
            R.raw.ajuno
        ),
        "AMAIERAKO_JARDUERA_3" to Informacion(
            getString(R.string.AMAIERAKO_JARDUERA_3),
            R.raw.ajtres
        )
    )


    var mediaPlayer: MediaPlayer? = null
    private var gameManagerService: GameManagerService? = GameManagerService()
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        intent.getDoubleExtra("punto", 0.0)

        gameManagerService = GameManager.get()
        progressBar = findViewById(R.id.ordenarImagenesProgressBar)
        gameManagerService!!.setInitialProgress(progressBar)

        val pantalla = gameManagerService?.pantallaActual()

        findViewById<TextView>(R.id.info).text = informacion[pantalla]?.texto ?: "Nulo"

        setupHeaderFragment(savedInstanceState)
        // Reemplaza el contenedor con el TitleFragment
        val fragmentContainer = findViewById<FrameLayout>(R.id.titleFragmentTag)

        if (savedInstanceState == null) {
            val titleFragment = TitleFragment.newInstance("Info")
            supportFragmentManager.beginTransaction()
                .replace(fragmentContainer.id, titleFragment, "titleFragmentTag")
                .commit()
        }

        // Configura el click listener para el botón en el fragmento
        val titleFragment =
            supportFragmentManager.findFragmentByTag("titleFragmentTag") as TitleFragment?
        titleFragment?.setOnHomeButtonClickListener(View.OnClickListener {

            NavigationUtil.navigateToMainMenu(this)
        })

        //Audios
        mediaPlayer = MediaPlayer.create(this, informacion[pantalla]?.audio!!)
        mediaPlayer?.start()

        val playaudio = findViewById<ImageButton>(R.id.playaudio)
        val pauseaudio = findViewById<ImageButton>(R.id.pauseaudio)

        playaudio.setOnClickListener{
            mediaPlayer?.start()
            playaudio.setColorFilter(resources.getColor(R.color.verdeOscuro), PorterDuff.Mode.SRC_IN)
            pauseaudio.setColorFilter(resources.getColor(R.color.grisOscuro), PorterDuff.Mode.SRC_IN)
        }
        pauseaudio.setOnClickListener{
            mediaPlayer?.pause()

            pauseaudio.setColorFilter(resources.getColor(R.color.verdeOscuro), PorterDuff.Mode.SRC_IN)
            playaudio.setColorFilter(resources.getColor(R.color.grisOscuro), PorterDuff.Mode.SRC_IN)
        }

        playaudio.setColorFilter(resources.getColor(R.color.verdeOscuro), PorterDuff.Mode.SRC_IN)
        pauseaudio.setColorFilter(resources.getColor(R.color.grisOscuro), PorterDuff.Mode.SRC_IN)

        //Boton terminar
        findViewById<Button>(R.id.terminar_info).setOnClickListener{
            gameManagerService?.addProgress(progressBar)
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null

            GameManager.get()?.nextScreen()
        }
    }

    private fun setupHeaderFragment(savedInstanceState: Bundle?) {
        val fragmentContainer = findViewById<FrameLayout>(R.id.titleFragmentTag)
        if (savedInstanceState == null) {
            val titleFragment = TitleFragment.newInstance(resources.getString(R.string.infoTitle))
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
    override fun onDestroy() {
        super.onDestroy()

        // Liberar los recursos del MediaPlayer cuando la actividad se destruye
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onPause() {
        super.onPause()

        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    data class Informacion(val texto: String, val audio: Int? = null)
}