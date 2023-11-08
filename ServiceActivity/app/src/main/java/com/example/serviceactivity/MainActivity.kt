package com.example.serviceactivity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    lateinit var platbtn: Button
    lateinit var contador: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        platbtn = findViewById(R.id.play)
        contador = findViewById(R.id.contador)

        //Boton de inicio
        platbtn.setOnClickListener{
            val minutos = findViewById<EditText>(R.id.minutos).text.toString().toInt()
            val segundos = findViewById<EditText>(R.id.segundos).text.toString().toInt()

            val serviceIntent = Intent(this, countdownService::class.java)
            serviceIntent.putExtra(countdownService.TIME_EXTRA, (minutos * 60 + segundos) * 1000L)
            startService(serviceIntent)

            contador.visibility = View.VISIBLE
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

        findViewById<Button>(R.id.cambiar_1).setOnClickListener {
            val intent = Intent(this, Prueba::class.java)
            startActivity(intent)
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
        const val ACTION_UPDATE = "com.example.serviceactivity.UPDATE"
        const val TIME_EXTRA = "com.example.serviceactivity.TIME_EXTRA"
    }

    // Registra un receptor de broadcast
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ACTION_FIN -> {
                    platbtn.isEnabled = true
                    contador.visibility = View.INVISIBLE
                }

                ACTION_UPDATE -> {
                    contador.text = intent.getLongExtra(TIME_EXTRA, 0).toString()

                }

            }

        }
    }
}