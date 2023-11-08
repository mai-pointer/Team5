package com.example.serviceactivity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {

    lateinit var platbtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        platbtn = findViewById(R.id.play)

        //Boton de inicio
        platbtn.setOnClickListener{
            val minutos = findViewById<EditText>(R.id.minutos).text.toString().toInt()
            val segundos = findViewById<EditText>(R.id.segundos).text.toString().toInt()

            val serviceIntent = Intent(this, countdownService::class.java)
            serviceIntent.putExtra(countdownService.TIME_EXTRA, (minutos * 60 + segundos) * 1000L)
            startService(serviceIntent)

            platbtn.isEnabled = false
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
        findViewById<Button>(R.id.stop).setOnClickListener {
            val serviceIntent = Intent(this, countdownService::class.java)
            serviceIntent.action = countdownService.ACTION_STOP
            startService(serviceIntent)

            platbtn.isEnabled = true
        }

    }

    //BROADCAST EVENTS
    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter(ACTION_FIN)
        registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(broadcastReceiver)
    }

    // Define una acción de broadcast
    companion object {
        const val ACTION_FIN = "com.example.serviceactivity.FIN"
    }

    // Registra un receptor de broadcast
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_FIN) {
                platbtn.isEnabled = true
            }
        }

    }
}