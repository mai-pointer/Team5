package com.example.didaktikapp

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        val buttonJugar: Button = findViewById(R.id.btnJugar)
        val buttonAjustes: Button = findViewById(R.id.ajustesBtn)

        // Inicia el servicio al comienzo de la aplicación
        startService(
            Intent(this, GameManagerService::class.java)
        )
        GameManager.initialize(this)

        // Inicializar la BD
        BDManager.context = applicationContext

        BDManager.Iniciar{ partidaDao, sharedPreferences ->
            GlobalScope.launch(Dispatchers.IO) {

                var partidas = partidaDao.getAll()

                if (partidas.size == 0){
                    val nuevaPartida = Partida(juego = "Juego1", pantalla = 0, hj = false, juegoMapa =  0)
                    partidaDao.insert(nuevaPartida)
                    sharedPreferences.edit().putInt("partida_id", 1).apply()

                    runOnUiThread {
                    }
                }
            }
        }


        // Agrega un OnClickListener al botón "Jugar"
        buttonJugar.setOnClickListener{
            // Crea un Intent para lanzar la actividad Jugar
            val intent = Intent(this@MainMenuActivity, MapsActivity::class.java)
            startActivity(intent)
        }
        // Agrega un OnClickListener al botón "Ajustes"
        buttonAjustes.setOnClickListener{
            // Crea un Intent para lanzar la actividad Ajustes
            val intent = Intent(this@MainMenuActivity, Ajustes::class.java)
            intent.putExtra("admin", false)
            startActivity(intent)
        }
        //Boton competitivo
        val buttonCompetitivo: Button = findViewById(R.id.competitivoBtn)
        buttonCompetitivo.setOnClickListener{
            GameManager.get()?.startGame("Competitivo")

            val serviceIntent = Intent(this, ServicioTiempo::class.java)
            startService(serviceIntent)
            bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE)
        }
    }

    override fun onDestroy() {
        // Asegúrate de desvincular el servicio cuando la actividad se destruye
        unbindService(serviceConnection)
        super.onDestroy()
    }


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

}