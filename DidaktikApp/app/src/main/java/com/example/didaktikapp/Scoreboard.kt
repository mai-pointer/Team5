package com.example.didaktikapp

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

// Asegúrate de agregar la dependencia en tu archivo build.gradle
// implementation("io.socket.client:socket.io-client:1.0.1")

class Scoreboard : AppCompatActivity() {

    private lateinit var scoresAdapter: ScoresAdapter
    private lateinit var socket: Socket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scoreboard)

// La mejor animación de la APP
        val imagen = findViewById<ImageView>(R.id.win)

// Animación de rotación en el eje X
        val rotateXAnimator = ObjectAnimator.ofFloat(imagen, View.ROTATION_X, 0f, -25f, 0f)
        rotateXAnimator.duration = 3000 // Ajusta la duración según tu preferencia (en milisegundos)

// Animación de escala (ampliación y desampliación)
        val scaleXAnimator = ObjectAnimator.ofFloat(imagen, View.SCALE_X, 1f, 1.2f, 1f)
        val scaleYAnimator = ObjectAnimator.ofFloat(imagen, View.SCALE_Y, 1f, 1.2f, 1f)
        scaleXAnimator.duration = 3000 // Ajusta la duración según tu preferencia
        scaleYAnimator.duration = 3000 // Ajusta la duración según tu preferencia

// Crea un conjunto de animaciones
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(rotateXAnimator, scaleXAnimator, scaleYAnimator)

// Configura el interpolador para que la animación sea más suave
        animatorSet.interpolator = AccelerateDecelerateInterpolator()

// Repite cada animación individualmente
        rotateXAnimator.repeatMode = ObjectAnimator.REVERSE
        rotateXAnimator.repeatCount = ObjectAnimator.INFINITE

        scaleXAnimator.repeatMode = ObjectAnimator.REVERSE
        scaleXAnimator.repeatCount = ObjectAnimator.INFINITE

        scaleYAnimator.repeatMode = ObjectAnimator.REVERSE
        scaleYAnimator.repeatCount = ObjectAnimator.INFINITE

// Inicia la animación
        animatorSet.start()

        // Obtén la referencia del ImageView
        val imageView = findViewById<ImageView>(R.id.gifconfeti)

        // Carga el GIF usando Glide
        Glide.with(this)
            .asGif()
            .load(R.drawable.confeti)
            .into(imageView)



        scoresAdapter = ScoresAdapter()

        val scoresRecyclerView: RecyclerView = findViewById(R.id.scoresRecyclerView)
        scoresRecyclerView.layoutManager = LinearLayoutManager(this)
        scoresRecyclerView.adapter = scoresAdapter

        // Inicializar el socket
        try {
            socket = IO.socket("https://lezamapp-pvp-mode.glitch.me")
            socket.connect()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Scoreboard", "Error al inicializar el socket: ${e.message}")
        }

        socket.on("scores") { args ->
            runOnUiThread {
                GlobalScope.launch(Dispatchers.Main) {
                    val scoresList = mutableListOf<Score>()

                    try {
                        val scoresList = mutableListOf<Score>()

                        if (args[0] is JSONArray) {
                            val scoresArray = args[0] as JSONArray

                            for (i in 0 until scoresArray.length()) {
                                try {
                                    val scoreObject = scoresArray.getJSONObject(i)

                                    // Log para verificar la cadena JSON recibida
                                    Log.d("Scoreboard", "JSON recibido para índice $i: $scoreObject")

                                    // Verifica si la clave "nombre" está presente en el objeto
                                    if (scoreObject.has("nombre")) {
                                        val name = scoreObject.getString("nombre")
                                        val score = scoreObject.getInt("puntuacion")
                                        val scoreItem = Score(name, score)
                                        scoresList.add(scoreItem)
                                    } else {
                                        Log.e("Scoreboard", "Error: No se encontró la clave 'nombre' en el objeto JSON.")
                                    }
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                    Log.e("Scoreboard", "Error al parsear el objeto JSON: ${e.message}")
                                }
                            }
                        } else if (args[0] is JSONObject) {
                            // Tratar el caso en el que args[0] es un JSONObject
                            val scoreObject = args[0] as JSONObject

                            // Log para verificar la cadena JSON recibida
                            Log.d("Scoreboard", "JSON recibido: $scoreObject")

                            // Verifica si la clave "nombre" está presente en el objeto
                            if (scoreObject.has("nombre")) {
                                val name = scoreObject.getString("nombre")
                                val score = scoreObject.getInt("puntuacion")
                                val scoreItem = Score(name, score)
                                scoresList.add(scoreItem)
                            } else {
                                Log.e("Scoreboard", "Error: No se encontró la clave 'nombre' en el objeto JSON.")
                            }
                        }

                        updateScoresList(scoresList)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Log.e("Scoreboard", "Error al parsear el objeto JSON: ${e.message}")
                    }



                    //updateScoresList(scoresList)
                }
            }
        }


        // Obtener puntuaciones desde el servidor usando corrutinas
        GlobalScope.launch(Dispatchers.Main) {
            val scoresList = getScoresFromServer()
            updateScoresList(scoresList)
        }

        if (intent.hasExtra("puntuacion")) {
            val puntuacion = intent.getIntExtra("puntuacion", 0)
            val nombre = "Jonsina"
            val jsonMessage = "{\"nombre\":\"$nombre\",\"puntuacion\":$puntuacion}"
            socket.emit("talk", jsonMessage)
        } else {
            Log.d("Scoreboard", "No se recibió la puntuación esperada.")
        }


    }

    fun updateScoresList(newList: List<Score>) {
        // Invertir el orden de la lista
        val listaInvertida = newList.toMutableList()
        listaInvertida.reverse()

        // Actualizar la lista en el adaptador y notificar los cambios
        scoresAdapter.submitList(listaInvertida)
        scoresAdapter.notifyDataSetChanged()

        Log.d("Scoreboard", "Lista de puntuaciones actualizada: $listaInvertida")
    }


    private suspend fun getScoresFromServer(): List<Score> {
        return withContext(Dispatchers.IO) {
            // Tu lógica para obtener puntuaciones desde el servidor

            // Devuelve una lista ficticia por ahora
            return@withContext listOf(
                Score("Cargando datos", 0),
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        socket.disconnect()
    }
}