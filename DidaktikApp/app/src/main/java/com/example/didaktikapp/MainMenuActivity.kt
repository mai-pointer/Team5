package com.example.didaktikapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainMenuActivity : AppCompatActivity() {
    private lateinit var buttonJugar: Button
    private lateinit var buttonAjustes: Button
    private lateinit var buttonCompetitivo: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        buttonJugar = findViewById(R.id.btnJugar)
        buttonAjustes = findViewById(R.id.ajustesBtn)
        buttonCompetitivo = findViewById(R.id.competitivoBtn)

        buttonJugar.isEnabled = false
        buttonAjustes.isEnabled = false
        buttonCompetitivo.isEnabled = false

        requestLocationPermision()

        // Inicia el servicio al comienzo de la aplicación
        startService(
            Intent(this, GameManagerService::class.java)
        )
        GameManager.initialize(this)

        // Inicializar la BD
        //BDManager.context = applicationContext

        BDManager.Iniciar(applicationContext){ partidaDao, competitivoDao, sharedPreferences ->
            GlobalScope.launch(Dispatchers.IO) {

                var partidas = partidaDao.getAll()

                if (partidas.size == 0){
                    val nuevaPartida = Partida(juego = "Juego1", pantalla = 0, hj = false, juegoMapa =  0)
                    partidaDao.insert(nuevaPartida)
                    sharedPreferences.edit().putInt("partida_id", 1).apply()
                }
            }
        }

        // Agrega un OnClickListener al botón "Jugar"
        buttonJugar.setOnClickListener{
            // Crea un Intent para lanzar la actividad Jugar
            val intent = Intent(this@MainMenuActivity, MapsActivity::class.java)
            startActivity(intent)
        }
        // Agrega un OnClickListener al botón "Ajustes"
        buttonAjustes.setOnClickListener{
            // Crea un Intent para lanzar la actividad Ajustes
            val intent = Intent(this@MainMenuActivity, Ajustes::class.java)
            intent.putExtra("admin", false)
            startActivity(intent)
        }

        buttonCompetitivo.setOnClickListener{
            GameManager.get()?.startGame("Competitivo")
        }
    }

    fun requestLocationPermision(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(this, "Lokalizatze baimenak baztertu dituzu. Mesedez onartu lokalizatze baimenak.", Toast.LENGTH_LONG).show()
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 777)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 777){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                buttonJugar.isEnabled = true
                buttonAjustes.isEnabled = true
                buttonCompetitivo.isEnabled = true
            }else{
                Toast.makeText(this, "Baimenak baztertu dituzu", Toast.LENGTH_LONG).show()
            }
        }

    }




}