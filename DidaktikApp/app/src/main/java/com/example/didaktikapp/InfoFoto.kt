package com.example.didaktikapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class InfoFoto : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_foto)

        findViewById<Button>(R.id.terminar_infofoto).setOnClickListener{
            GameManager.get()?.nextScreen()
        }
    }
}