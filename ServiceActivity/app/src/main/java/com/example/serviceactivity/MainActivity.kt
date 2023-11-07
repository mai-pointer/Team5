package com.example.serviceactivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Boton de inicio
        findViewById<Button>(R.id.play).setOnClickListener{
            val minutos = findViewById<EditText>(R.id.minutos).text.toString().toInt()
            val segundos = findViewById<EditText>(R.id.segundos).text.toString().toInt()

            val serviceIntent = Intent(this, countdownService::class.java)
            serviceIntent.putExtra(countdownService.TIME_EXTRA, (minutos * 60 + segundos) * 1000L)

            // Iniciar el servicio
            startService(serviceIntent)
        }
        findViewById<Button>(R.id.pause).setOnClickListener {
            val serviceIntent = Intent(this, countdownService::class.java)
            serviceIntent.action = countdownService.ACTION_PAUSE
            startService(serviceIntent)
        }
        findViewById<Button>(R.id.restart).setOnClickListener {
            val serviceIntent = Intent(this, countdownService::class.java)
            startService(serviceIntent)
        }

    }

    fun Finalizar(){

    }
}