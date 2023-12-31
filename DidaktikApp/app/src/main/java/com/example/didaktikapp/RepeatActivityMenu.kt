package com.example.didaktikapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.didaktikapp.navigation.NavigationUtil

class RepeatActivityMenu(private val activity: AppCompatActivity)  {
     fun showGameOverDialog(context: Context, intent: Intent) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Jokoa amaituta")
        builder.setMessage("Berriro jolastu nahi duzu?")
        builder.setPositiveButton("Reiniciar") { _, _ ->
            // Si el usuario hace clic en "Bai" (Sí), reinicia la actividad
            restartActivity(intent)
        }
        builder.setNegativeButton("Continuar") { _, _ ->
            // Si el usuario hace clic en "Ez" (No), continua con el juego
            GameManager.get()?.nextScreen()
        }

        // Mostrar el cuadro de diálogo
        builder.show()
    }

    private fun restartActivity(intent: Intent) {
        activity.finish()
        activity.startActivity(intent)
    }

}