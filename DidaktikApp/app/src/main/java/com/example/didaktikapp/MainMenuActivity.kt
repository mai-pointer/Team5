package com.example.didaktikapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.lifecycle.lifecycleScope
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

        //Crea la base de datos si no existe
        BDManager.inicializar(lifecycleScope)
        BDManager.partida(this){ sharedPreferences, partidaBD ->
            val juegoActualId = sharedPreferences.getInt("juego_actual", -1)

            if (juegoActualId == -1) {
                // No hay valor en SharedPreferences, crea un nuevo elemento en la base de datos.
                val nuevoJuego = Partida(0, "Juego1", 0)
                partidaBD.insert(nuevoJuego)

                // Guarda el ID del nuevo juego en SharedPreferences.
                val editor = sharedPreferences.edit()
                editor.putInt("juego_actual", nuevoJuego.id)
                editor.apply()
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
    }
}
