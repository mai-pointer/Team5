package com.example.didaktikapp

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.didaktikapp.navigation.NavigationUtil

class RepeatActivityMenu(private val activity: AppCompatActivity)  {
    fun showGameOverDialog(context: Context, intent: Intent) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(activity.getString(R.string.si).toUpperCase())
        builder.setMessage(activity.getString(R.string.continuar))

        // Cambia el color del texto del botón positivo
        val positiveButtonText = SpannableString(activity.getString(R.string.si))
        positiveButtonText.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.verdeOscuro)),
            0,
            positiveButtonText.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )

        builder.setPositiveButton(positiveButtonText) { _, _ ->
            // Continua con el juego
            GameManager.get()?.nextScreen()
        }

        builder.setNegativeButton(activity.getString(R.string.no)) { _, _ ->
            // Reinicia la actividad
            restartActivity(intent)
        }
        builder.setCancelable(false)

        // Mostrar el cuadro de diálogo
        builder.show()
    }

    private fun restartActivity(intent: Intent) {
        activity.finish()
        activity.startActivity(intent)
    }

}