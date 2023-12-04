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

        val buttonJugar: Button = findViewById(R.id.button)

        // Agrega un OnClickListener al botón "Jugar"
        buttonJugar.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                // Crea un Intent para lanzar la actividad MapsActivity
                val intent = Intent(this@MainMenuActivity, MapsActivity::class.java)
                startActivity(intent)
            }
        })
    }
}
