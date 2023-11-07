package com.example.serviceactivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
        findViewById<Button>(R.id.pause).setOnClickListener{

        }
        findViewById<Button>(R.id.restart).setOnClickListener{

        }
    }

    fun Finalizar(){

    }
}