package com.example.didaktikapp


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import com.google.android.material.switchmaterial.SwitchMaterial


class MainMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        val swDarkMode = findViewById<SwitchMaterial>(R.id.modoOscuro)

        swDarkMode.setOnCheckedChangeListener { _, isSelected ->
            if (isSelected) {
                enableDarkMode()
            } else {
                disbleDarkMode()
            }

            val buttonJugar: Button = findViewById(R.id.btnJugar)
            val buttonAjustes: Button = findViewById(R.id.ajustesBtn)

            // Inicia el servicio al comienzo de la aplicación
            startService(
                Intent(this, GameManagerService::class.java)
            )
            GameManager.initialize(this)

            // Agrega un OnClickListener al botón "Jugar"
            buttonJugar.setOnClickListener {
                // Crea un Intent para lanzar la actividad Jugar
                val intent = Intent(this@MainMenuActivity, MapsActivity::class.java)
                startActivity(intent)
            }
            // Agrega un OnClickListener al botón "Ajustes"
            buttonAjustes.setOnClickListener {
                // Crea un Intent para lanzar la actividad Ajustes
                val intent = Intent(this@MainMenuActivity, Ajustes::class.java)
                intent.putExtra("admin", false)
                startActivity(intent)
            }
        }
    }

    private fun enableDarkMode() {
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
        delegate.applyDayNight()
    }

    private fun disbleDarkMode() {
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
        delegate.applyDayNight()
    }
}
