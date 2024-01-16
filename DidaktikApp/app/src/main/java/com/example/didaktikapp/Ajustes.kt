package com.example.didaktikapp

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.switchmaterial.SwitchMaterial
import java.util.Locale

class Ajustes : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var botonCastellano: ImageButton
    private lateinit var botonEuskera: ImageButton
    private lateinit var botonIngles: ImageButton
    private var idiomaPredeterminado: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajustes)

        // Inicializa sharedPreferences antes de acceder a ella
        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE)

        // Obtén el idioma predeterminado antes de cambiarlo
        idiomaPredeterminado = sharedPreferences.getString("language", "eu")


        botonCastellano = findViewById(R.id.botonCastellano)
        botonEuskera = findViewById(R.id.botonEuskera)
        botonIngles = findViewById(R.id.botonIngles)

        // Configurar el botón para cambiar el idioma
        botonCastellano.setOnClickListener {
            cambiarIdioma("es")
            // Puedes reiniciar la actividad o la aplicación después de cambiar el idioma si es necesario
            restartActivity()
        }
        botonEuskera.setOnClickListener {
            cambiarIdioma("eu")
            restartActivity()
        }
        botonIngles.setOnClickListener {
            cambiarIdioma("eng")
            restartActivity()
        }


        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE)

        val swDarkMode = findViewById<SwitchMaterial>(R.id.modoOscuro)
        swDarkMode.isChecked = sharedPreferences.getBoolean("isDarkModeOn", false)

        swDarkMode.setOnCheckedChangeListener { _, isSelected ->
            if (isSelected) {
                enableDarkMode()
            } else {
                disableDarkMode()
            }
        }

        // Salir
        findViewById<Button>(R.id.salirBtn).setOnClickListener {
            val intent = Intent(this@Ajustes, MainMenuActivity::class.java)
            startActivity(intent)
        }

        // Modo profesor
        findViewById<RelativeLayout>(R.id.profesores).setOnClickListener {
            val intent = Intent(this@Ajustes, AdminLoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun enableDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        delegate.applyDayNight()
        saveDarkModeState(true)
    }

    private fun disableDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        delegate.applyDayNight()
        saveDarkModeState(false)
    }

    private fun saveDarkModeState(isDarkModeOn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isDarkModeOn", isDarkModeOn)
        editor.apply()
    }

    private fun cambiarIdioma(languageCode: String) {
        try {
            val locale = Locale(languageCode)
            Locale.setDefault(locale)

            val configuration = Configuration(resources.configuration)
            configuration.setLocale(locale)

            val context = createConfigurationContext(configuration)
            resources.updateConfiguration(configuration, resources.displayMetrics)
            saveLanguagePreference(languageCode) // Guarda la preferencia del idioma

            // Agrega logs para verificar si se llega a este punto
            Log.d("Ajustes", "Idioma cambiado correctamente a $languageCode")
        } catch (e: Exception) {
            // Captura cualquier excepción y agrega un log
            Log.e("Ajustes", "Error al cambiar el idioma: ${e.message}", e)
        }
    }

    private fun saveLanguagePreference(languageCode: String) {
        val editor = sharedPreferences.edit()
        editor.putString("language", languageCode)
        editor.apply()
    }

    private fun restartApplication() {
        val intent = Intent(applicationContext, Ajustes::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finishAffinity()
    }

    private fun restartActivity() {
        restartApplication()
    }
}
