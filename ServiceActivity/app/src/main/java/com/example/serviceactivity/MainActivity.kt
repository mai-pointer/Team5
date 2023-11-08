package com.example.serviceactivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class MainActivity : AppCompatActivity(), CountdownListener {
    private lateinit var contador : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        contador = findViewById<TextView>(R.id.contador)

        //Boton de inicio
        findViewById<Button>(R.id.play).setOnClickListener{
            val minutos = findViewById<EditText>(R.id.minutos).text.toString().toInt()
            val segundos = findViewById<EditText>(R.id.segundos).text.toString().toInt()

            val serviceIntent = Intent(this, countdownService::class.java)
            serviceIntent.putExtra(countdownService.TIME_EXTRA, (minutos * 60 + segundos) * 1000L)

            // Iniciar el servicio
            startService(serviceIntent)
            contador.visibility = View.VISIBLE
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

    override fun onTimeTick(timeRemaining: Long) {
        val minutes = (timeRemaining / 60).toInt()
        val seconds = (timeRemaining % 60).toInt()
        val timeString = String.format("%02d:%02d", minutes, seconds)
        contador.text = timeString
    }


}