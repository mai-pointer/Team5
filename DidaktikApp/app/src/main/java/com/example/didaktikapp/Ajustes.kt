package com.example.didaktikapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.Toast

class Ajustes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajustes)

        //Salir
        findViewById<RelativeLayout>(R.id.salirBtn).setOnClickListener{
            val intent = Intent(this@Ajustes, MainMenuActivity::class.java)
            startActivity(intent)
        }

        //Modo profesor
        findViewById<RelativeLayout>(R.id.profesores).setOnClickListener{
            val intent = Intent(this@Ajustes, MapsActivity::class.java)
            startActivity(intent)
        }
    }
}