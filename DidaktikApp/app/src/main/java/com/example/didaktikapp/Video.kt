package com.example.didaktikapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Video : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        //Boton terminar
        findViewById<Button>(R.id.terminar_video).setOnClickListener{
            GameManager.get()?.nextScreen()
        }
    }
}