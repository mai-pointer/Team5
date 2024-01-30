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




    var mediaPlayer: MediaPlayer? = null
    private var gameManagerService: GameManagerService? = GameManagerService()
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        val informacion = hashMapOf<String, Informacion>(
            "Juego1.2" to Informacion(
                resources.getString(R.string.Juego1_2),
                R.raw.juegounodos
            ),
            "Juego2.2" to Informacion(
                resources.getString(R.string.Juego2_2),
                R.raw.juegodosuno
            ),
            "Juego2.4" to Informacion(
                resources.getString(R.string.Juego2_4),
                R.raw.juegodoscuatro
            ),
            "Juego3.2" to Informacion(
                resources.getString(R.string.Juego3_2),
                R.raw.juegotresdos
            ),
            "Juego3.3" to Informacion(
                resources.getString(R.string.Juego3_3),
                R.raw.juegotrestres
            ),
            "Juego3.5" to Informacion(
                resources.getString(R.string.Juego3_5),
                R.raw.juegotrescinco
            ),
            "Juego4.1" to Informacion(
                resources.getString(R.string.Juego4_1),
                R.raw.juegocuatrouno
            ),
            "Juego4.4" to Informacion(
                resources.getString(R.string.Juego4_4),
                R.raw.juegocuatrocuatro
            ),
            "Juego5.1" to Informacion(
                resources.getString(R.string.Juego5_1),
                R.raw.juegocincouno
            ),
            "Juego5.3" to Informacion(
                resources.getString(R.string.Juego5_3),
                R.raw.juegocincotres
            ),
            "Juego5.6" to Informacion(
                resources.getString(R.string.Juego5_6),
                R.raw.juegocincoseis
            ),
            "Juego6.1" to Informacion(
                resources.getString(R.string.Juego6_1),
                R.raw.juegoseisuno
            ),
            "Juego6.3" to Informacion(
                resources.getString(R.string.Juego6_3),
                R.raw.juegoseistres
            ),
            "Juego7.1" to Informacion(
                resources.getString(R.string.Juego7_1),
                R.raw.juegosieteuno
            ),
            "HASIERAKO JARDUERA.1" to Informacion(
                resources.getString(R.string.HASIERAKO_JARDUERA_1),
                R.raw.hjuno
            ),
            "AMAIERAKO JARDUERA.1" to Informacion(
                resources.getString(R.string.AMAIERAKO_JARDUERA_1),
                R.raw.ajuno
            ),
            "AMAIERAKO JARDUERA.3" to Informacion(
                resources.getString(R.string.AMAIERAKO_JARDUERA_3),
                R.raw.ajtres
            )
        )

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
        informacion[pantalla]?.audio!!
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