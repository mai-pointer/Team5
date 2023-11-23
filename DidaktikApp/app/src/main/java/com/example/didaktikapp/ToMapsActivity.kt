package com.example.didaktikapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class ToMapsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tomaps)

        val btnGoToMaps = findViewById<Button>(R.id.button)

        btnGoToMaps.setOnClickListener {
            // Crea un Intent para iniciar MapsActivity
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

    }
}