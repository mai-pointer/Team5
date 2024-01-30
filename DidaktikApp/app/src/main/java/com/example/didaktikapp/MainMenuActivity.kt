package com.example.didaktikapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
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

        BDManager.Iniciar{ partidaDao, competitivoDao, sharedPreferences ->
            GlobalScope.launch(Dispatchers.IO) {

                var partidas = partidaDao.getAll()

                if (partidas.size == 0){
                    val nuevaPartida = Partida(juego = "Juego1", pantalla = 0, hj = false, juegoMapa =  0)
                    partidaDao.insert(nuevaPartida)
                    sharedPreferences.edit().putInt("partida_id", 1).apply()
                }
            }
        }




        // Agrega un OnClickListener al botón "Jugar"
        buttonJugar.setOnClickListener{
            val sharedPreferences = getSharedPreferences("mySharedPrefs", Context.MODE_PRIVATE)
            val hj = sharedPreferences.getBoolean("hj", false)
            if (hj){
                val intent = Intent(this@MainMenuActivity, MapsActivity::class.java)
                intent.putExtra("admin", false)
                startActivity(intent)
            } else {
                GameManager.get()?.startGame("HASIERAKO JARDUERA")
                val editor = sharedPreferences.edit()
                editor.putBoolean("hj", true)
                editor.apply()
            }

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
        }
    }




}