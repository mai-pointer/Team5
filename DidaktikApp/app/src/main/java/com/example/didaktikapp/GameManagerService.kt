package com.example.didaktikapp

import android.animation.ObjectAnimator
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ProgressBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GameManagerService : Service() {

    //Almacena la información de los juegos
    val games = mapOf(
        "Idi probak" to listOf(
            PreguntasPistas::class.java,
            Info::class.java,
            MultipleChoiceActivity::class.java,
            MultipleChoiceActivity::class.java,
            MultipleChoiceActivity::class.java,
            MultipleChoiceActivity::class.java
        ),
        "Odolostea" to listOf(
            PreguntasPistas::class.java,
            Info::class.java,
            WordSearchActivity::class.java,
            Info::class.java
        ),
        "Txakoli" to listOf(
            PreguntasPistas::class.java,
            Info::class.java,
            Info::class.java,
            Video::class.java,
            Info::class.java,
        ),
        "Udala" to listOf(
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
        "Santa Maria" to listOf(
            Info::class.java,
            PreguntasPistas::class.java,
            Info::class.java,
            Video::class.java,
            PuzzleActivity::class.java,
            Info::class.java,
        ),
        "San Mameseko Arkua" to listOf(
            Info::class.java,
            Crucigrama::class.java,
            Info::class.java,
        ),
        "Lezamako dorrea" to listOf(
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
            )
    )

    //Variables
    private lateinit var context: Context
    var juegoActual: List<Class<*>>? = null
    private var pantallaActual = 0
    private var nombreJuego: String = ""
    private var screenCount: Int? = 0
    private var admin: Boolean = false

    //Inicializa el servicio
    fun initialize(context: Context) {
        this.context = context
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    //Inicia el juego seleccionado
    fun startGame(gameName: String, esAdmin : Boolean = false) {

        //En caso de que sea modo competitivo
        if("Competitivo" == gameName)
        {
            context
            val serviceIntent = Intent(context, ServicioTiempo::class.java)
            context.startService(serviceIntent)
            context.bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE)
        }

        nombreJuego = gameName
        juegoActual = games[gameName]
        pantallaActual = 0
        screenCount = juegoActual?.size
        admin = esAdmin
        pantalla()

        guardar()
    }

    //Pasa a la siguiente pantalla
    fun nextScreen() {
        if (pantallaActual < (juegoActual?.size ?: 0) - 1) {
            //Si hay más pantallas, pasa a la siguiente
            pantallaActual++
            pantalla()
        }
        //Termina el juego
        else {
            //En caso de que sea modo competitivo
            if("Competitivo" == nombreJuego)
            {
                if (servicio_activo) {
                    val tiempo = servicio_tiempo.Detener()
                    context.unbindService(serviceConnection)
                    servicio_activo = false

                    BDManager.Iniciar { partidaDao, competitivoDao, sharedPreferences ->

                        val nuevoTiempo =  Competitivo(tiempo = tiempo, partidaId = sharedPreferences.getInt("partida_id", 1))
                        GlobalScope.launch(Dispatchers.IO) {
                            competitivoDao.insert(nuevoTiempo)
                        }

                    }

                    //PASAR AL SERVER EL TIEMPO
                    val intent = Intent(context, Scoreboard::class.java)
                    var numeroToPotente = tiempo.toString().toInt()
                    intent.putExtra("puntuacion", numeroToPotente)
                    context.startActivity(intent)
                }
                else{
                    BDManager.Iniciar{ partidaDao, CompetitivoDao, sharedPreferences ->
                        GlobalScope.launch(Dispatchers.IO) {
                            val partida = partidaDao.get(sharedPreferences.getInt("partida_id", -1))

                            val myIndex = games.keys.indexOf(partida.juego)
                            nombreJuego = games.keys.elementAt(myIndex + 1)

                            guardar()
                        }
                    }
                }

                //Si no, vuelve al menú principal
                pantallaActual = 0
            }
            //En caso de que sea un juego normal
            else{
                if (!admin){
                    //Cambia el index del mapa
                    var mapManagerService = MapManagerService.MapManager.get()

                    if(mapManagerService == null){
                        MapManagerService.MapManager.initialize(this, false)
                        mapManagerService = MapManagerService.MapManager.get()
                    }
                    mapManagerService!!.showNextLocation()

//                    val intent = Intent(context, MapsActivity::class.java)
//                    context.startActivity(intent)
                } else{
                    val intent = Intent(context, MapsActivity::class.java)
                    intent.putExtra("admin", true)
                    context.startActivity(intent)
                }
            }
        }

        guardar()
    }

    fun guardar()
    {
        BDManager.Iniciar{ partidaDao, competitivoDao, sharedPreferences ->
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
        Log.i("FALLO-GM", cambiarNombre(games.entries.find { it.value == juegoActual }?.key.toString()) + "." + (pantallaActual+1).toString())
        return  cambiarNombre(games.entries.find { it.value == juegoActual }?.key.toString()) + "." + (pantallaActual+1).toString()
    }

    fun juegoActual(): String {
        return cambiarNombre(games.entries.find { it.value == juegoActual }?.key.toString())
    }

    fun cambiarNombre(nombre : String) : String{
        return when (nombre) {
            "Idi probak" -> "Juego1"
            "Odolostea" -> "Juego2"
            "Txakoli" -> "Juego3"
            "Udala" -> "Juego4"
            "Santa Maria" -> "Juego5"
            "San Mameseko Arkua" -> "Juego6"
            "Lezamako dorrea" -> "Juego7"
            else -> nombre
        }
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
        context.unbindService(serviceConnection)
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
