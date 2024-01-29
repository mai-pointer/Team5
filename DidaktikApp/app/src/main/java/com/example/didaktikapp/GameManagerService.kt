package com.example.didaktikapp

import android.animation.ObjectAnimator
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ProgressBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GameManagerService : Service() {

    //Almacena la información de los juegos
    val games = mapOf(
        "Juego1" to listOf(
            PreguntasPistas::class.java,
            Info::class.java,
            MultipleChoiceActivity::class.java,
            MultipleChoiceActivity::class.java,
            MultipleChoiceActivity::class.java,
            MultipleChoiceActivity::class.java
        ),
        "Juego2" to listOf(
            PreguntasPistas::class.java,
            Info::class.java,
            WordSearchActivity::class.java,
            Info::class.java
        ),
        "Juego3" to listOf(
            PreguntasPistas::class.java,
            Info::class.java,
            Info::class.java,
            Video::class.java,
            Info::class.java,
        ),
        "Juego4" to listOf(
            Info::class.java,
            DiferenciasActivity::class.java,
            MultipleChoiceActivity::class.java,
            MultipleChoiceActivity::class.java,
            MultipleChoiceActivity::class.java,
            MultipleChoiceActivity::class.java,
            MultipleChoiceActivity::class.java,
            MultipleChoiceActivity::class.java,
            Info::class.java
        ),
        "Juego5" to listOf(
            Info::class.java,
            PreguntasPistas::class.java,
            Info::class.java,
            Video::class.java,
            PuzzleActivity::class.java,
            Info::class.java,
        ),
        "Juego6" to listOf(
            Info::class.java,
            Crucigrama::class.java,
            Info::class.java,
        ),
        "Juego7" to listOf(
            Info::class.java,
            InsertWordsActivity::class.java,
            JuegoTorre::class.java,
        ),
        "HASIERAKO JARDUERA" to listOf(
            Info::class.java,
        ),
        "AMAIERAKO JARDUERA" to listOf(
            Info::class.java,
            OrdenarImagenesActivity::class.java,
            Info::class.java,
        )
    )

    //Variables
    private lateinit var context: Context
    var juegoActual: List<Class<*>>? = null
//    private var juegoNumero = 0
    private var pantallaActual = 0
    private var nombreJuego: String = ""
    private var screenCount: Int? = 0

    //Inicializa el servicio
    fun initialize(context: Context) {
        this.context = context
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    //Inicia el juego seleccionado
    fun startGame(gameName: String) {
        nombreJuego = gameName
        juegoActual = games[gameName]
        pantallaActual = 0
        screenCount = juegoActual?.size
        pantalla()

        guardar()
    }

    //Pasa a la siguiente pantalla
    fun nextScreen() {
        if (pantallaActual < (juegoActual?.size ?: 0) - 1) {
            //Si hay más pantallas, pasa a la siguiente
            pantallaActual++
            pantalla()
        } else {
            //Si no, vuelve al menú principal
            pantallaActual = 0

            val intent = Intent(context, MapsActivity::class.java)
            context.startActivity(intent)
        }

        guardar()
    }

    fun guardar()
    {
        BDManager.Iniciar{ partidaDao, sharedPreferences ->
            GlobalScope.launch(Dispatchers.IO){
                partidaDao.update(
                    Partida(
                        id = sharedPreferences.getInt("partida_id", 1),
                        juego = nombreJuego,
                        pantalla = pantallaActual,
                        tiempo = 0.0f
                    )
                )
            }
        }
    }

    //Lanza la pantalla actual
    private fun pantalla() {
        juegoActual?.let {
            //Lanza la pantalla actual
            val screenClass = it[pantallaActual]
            val screenIntent = Intent(context, screenClass)
            screenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(screenIntent)
        }
    }

    //Devuelve el número de pantalla actual
    fun pantallaActual(): String {
        return  games.entries.find { it.value == juegoActual }?.key + "." + (pantallaActual+1).toString()
    }

    fun juegoActual(): String {
        return  games.entries.find { it.value == juegoActual }?.key.toString()
    }

    fun getCurrentScreenIndex():Int{
        return pantallaActual
    }

    fun getTotalScreenIndex():Int{
        return screenCount!!
    }

    fun setInitialProgress(progressBar: ProgressBar) {
        val normalizedProgress = if (pantallaActual > screenCount!!) screenCount!! else pantallaActual
        // Establece el progreso inicial
        progressBar.progress = normalizedProgress
        progressBar.max = screenCount!!
    }

    fun addProgress(progressBar: ProgressBar){
        val newProgress = pantallaActual + 1

        GlobalScope.launch(Dispatchers.Main) {
            val progressAnimator =
                ObjectAnimator.ofInt(progressBar, "progress", pantallaActual, newProgress)
            progressAnimator.duration = 1000
            progressAnimator.interpolator = AccelerateDecelerateInterpolator()
            progressAnimator.start()
        }
    }
}

//Singleton
object GameManager {
    private var gameManagerService: GameManagerService? = null

    //Inicializa el singleton
    fun initialize(context: Context) {
        if (gameManagerService == null) {
            gameManagerService = GameManagerService()
            gameManagerService?.initialize(context)
        }
    }

    //Devuelve el servicio
    fun get(): GameManagerService? {
        return gameManagerService
    }
}
