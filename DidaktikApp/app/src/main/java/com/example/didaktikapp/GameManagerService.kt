package com.example.didaktikapp

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
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
        ),
        "Competitivo" to listOf(
            WordSearchActivity::class.java,
            DiferenciasActivity::class.java,
            PuzzleActivity::class.java,
            Crucigrama::class.java,
            InsertWordsActivity::class.java,
            JuegoTorre::class.java,
            OrdenarImagenesActivity::class.java,
//          Info::class.java,
        )
    )

    //Variables
    private lateinit var context: Context
    var juegoActual: List<Class<*>>? = null
    private var pantallaActual = 0
    private var nombreJuego: String = ""

    //Inicializa el servicio
    fun initialize(context: Context) {
        this.context = context
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    //Inicia el juego seleccionado
    fun startGame(gameName: String) {

        //En caso de que sea modo competitivo
        if("Competitivo" == gameName)
        {
            context
            val serviceIntent = Intent(context, ServicioTiempo::class.java)
            context.startService(serviceIntent)
            context.bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE)
        }

        //nombreJuego = gameName
        juegoActual = games[gameName]
        pantallaActual = 0
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

            BDManager.Iniciar{ partidaDao, sharedPreferences ->
                GlobalScope.launch(Dispatchers.IO) {
                    val partida = partidaDao.get(sharedPreferences.getInt("partida_id", -1))

                    val myIndex = games.keys.indexOf(partida.juego)
                    nombreJuego = games.keys.elementAt(myIndex + 1)

                    guardar()
                }
            }


            //En caso de que sea modo competitivo
            if("Competitivo" == nombreJuego)
            {
                if (servicio_activo) {
                    val tiempo = servicio_tiempo.Detener()
                    Log.i("TIEMPO", tiempo)
                    unbindService(serviceConnection)
                    servicio_activo = false

                    //************VAS AL SCOREBOARD************
                    val intent = Intent(context, MainMenuActivity::class.java)
                    context.startActivity(intent)
                    //************VAS AL SCOREBOARD************
                }
            }
            else{
                val intent = Intent(context, MapsActivity::class.java)
                context.startActivity(intent)
            }
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
                        juegoMapa = 0,
                        hj = true
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

    //VARIABLES DEL SERVICIO

    private lateinit var servicio_tiempo: ServicioTiempo
    private var servicio_activo = false
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as ServicioTiempo.LocalBinder
            servicio_tiempo = binder.getService()
            servicio_activo = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            servicio_activo = false
        }
    }

    override fun onDestroy() {
        // Asegúrate de desvincular el servicio cuando la actividad se destruye
        unbindService(serviceConnection)
        super.onDestroy()
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
