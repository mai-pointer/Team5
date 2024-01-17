package com.example.didaktikapp

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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

        BDManager.Iniciar{ partidaDao, sharedPreferences ->

            //Carga los datos en el spinner
            CargarLista(partidaDao, sharedPreferences)

            // Configurar el botón para crear una nueva partida
            val btnNuevaPartida: Button = findViewById(R.id.nuevapartida)
            btnNuevaPartida.setOnClickListener {
                NuevaPartida(partidaDao, sharedPreferences)
            }

            //Configura el botón para borrar la partida actual
            val btnBorrarPartida: Button = findViewById(R.id.eliminarpartida)
            btnBorrarPartida.setOnClickListener {
                GlobalScope.launch(Dispatchers.IO) {

                    if(partidaDao.getAll().size <= 1) {
                        runOnUiThread {
                            Toast.makeText(this@Ajustes, getString(R.string.error_ajustes_1), Toast.LENGTH_SHORT).show()
                        }
                        return@launch
                    }

                    var partida_id = sharedPreferences.getInt("partida_id", -1)

                    partidaDao.delete(partidaDao.get(partida_id))
                    sharedPreferences.edit().putInt("partida_id", partidaDao.getAll()[0].id).apply()
                    runOnUiThread {
                        CargarLista(partidaDao, sharedPreferences)
                    }
                }
            }
        }

    }

    fun CargarLista(partidaDao: PartidaDao, sharedPreferences: SharedPreferences)
    {
        GlobalScope.launch(Dispatchers.IO) {
            //Carga los datos de la BD
            val partidas = mutableListOf<Partida>()
            partidas.addAll(partidaDao.getAll())

            runOnUiThread {
                // Configurar el Spinner
                val spinner: Spinner = findViewById(R.id.listapartidas)
                val adapter = ArrayAdapter(this@Ajustes, android.R.layout.simple_spinner_item, partidas.map { getString(R.string.partida) + " " + it.id.toString() })
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter

                // Seleccionar la partida actual
                var partida_id = sharedPreferences.getInt("partida_id", -1)

                val posicion_actual = partidas.indexOfFirst { it.id == partida_id }
                if (posicion_actual != -1) {
                    spinner.setSelection(posicion_actual)
                }

                // Configurar el OnItemSelectedListener para el Spinner
                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {

                        val selectedPartidaId = partidas[position].id
                        sharedPreferences.edit().putInt("partida_id", selectedPartidaId).apply()

                        //ELIMINAR ****************************************************
                        GlobalScope.launch(Dispatchers.IO){
                            val partida = partidaDao.get(selectedPartidaId)
                            runOnUiThread {
                                Toast.makeText(this@Ajustes, "Partida seleccionada: ${partida.juego} - ${partida.pantalla}", Toast.LENGTH_SHORT).show()
                            }
                        }
                        //ELIMINAR ****************************************************
                    }

                    override fun onNothingSelected(parentView: AdapterView<*>){}
                }
            }
        }
    }

    fun NuevaPartida(partidaDao: PartidaDao, sharedPreferences: SharedPreferences)
    {
        val nuevaPartida =  Partida(juego = "Juego1", pantalla = 0, hj = false)
        GlobalScope.launch(Dispatchers.IO) {
            partidaDao.insert(nuevaPartida)
            runOnUiThread {
                sharedPreferences.edit().putInt("partida_id", nuevaPartida.id).apply()
                CargarLista(partidaDao, sharedPreferences)
            }
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
