package com.example.didaktikapp

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder

class GameManagerService : Service() {

    //Almacena la información de los juegos
    val games = mapOf(
        "Juego1" to listOf(
            PreguntasPistas::class.java,
            Info::class.java,
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
            Info::class.java
        ),
        "Juego5" to listOf(
            Info::class.java,
            Info::class.java,
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
            Info::class.java,
            Info::class.java,
            InsertWordsActivity::class.java,
            OrdenarImagenesActivity::class.java,
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
    private var juegoActual: List<Class<*>>? = null
    private var juegoNumero = 0
    private var pantallaActual = 0

    //Inicializa el servicio
    fun initialize(context: Context) {
        this.context = context
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    //Inicia el juego seleccionado
    fun startGame(gameName: String) {
        juegoActual = games[gameName]
        pantallaActual = 0
        pantalla()
    }

    //Pasa a la siguiente pantalla
    fun nextScreen() {
        if (pantallaActual < (juegoActual?.size ?: 0) - 1) {
            //Si hay más pantallas, pasa a la siguiente
            pantallaActual++
            pantalla()
        } else {
            //Si no, vuelve al menú principal
            val intent = Intent(context, MapsActivity::class.java)
            context.startActivity(intent)
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
