package com.example.didaktikapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class PreguntasPistas : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preguntas_pistas)

        findViewById<Button>(R.id.terminar_preguntapista).setOnClickListener{
            GameManager.get()?.nextScreen()
        }
    }
}