package com.example.didaktikapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnGoToMaps = findViewById<Button>(R.id.button)

        btnGoToMaps.setOnClickListener {
            // Crea un Intent para iniciar MapsActivity
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

    }
}