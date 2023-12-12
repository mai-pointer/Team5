package com.example.didaktikapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        val buttonJugar: Button = findViewById(R.id.btnJugar)
        val buttonAjustes: Button = findViewById(R.id.ajustesBtn)

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
            startActivity(intent)
        }
    }
}
