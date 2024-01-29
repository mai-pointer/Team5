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

        // Escuchar actualizaciones de puntuaciones desde el servidor
        socket.on("scores") { args ->
            runOnUiThread {
                GlobalScope.launch(Dispatchers.Main) {
                    val scoresArray = args[0] as JSONArray
                    val scoresList = mutableListOf<Score>()

                    for (i in 0 until scoresArray.length()) {
                        try {
                            val scoreObject = scoresArray.getJSONObject(i)
                            val name = scoreObject.getString("nombre")
                            val score = scoreObject.getInt("puntuacion")
                            val scoreItem = Score(name, score)
                            scoresList.add(scoreItem)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            Log.e("Scoreboard", "Error al parsear el objeto JSON: ${e.message}")
                        }
                    }

                    updateScoresList(scoresList)
                }
            }
        }

        // Obtener puntuaciones desde el servidor usando corrutinas
        GlobalScope.launch(Dispatchers.Main) {
            val scoresList = getScoresFromServer()
            updateScoresList(scoresList)
        }
    }

    private fun updateScoresList(newList: List<Score>) {
        // Actualizar la lista en el adaptador y notificar los cambios
        scoresAdapter.submitList(newList)
        scoresAdapter.notifyDataSetChanged()
        Log.d("Scoreboard", "Lista de puntuaciones actualizada: $newList")
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


/*
    class Scoreboard : AppCompatActivity() {

        private lateinit var scoresAdapter: ScoresAdapter
        private lateinit var socket: Socket

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_scoreboard)

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

            // Obtener puntuaciones desde el servidor usando corrutinas
            GlobalScope.launch(Dispatchers.Main) {
                val scoresList = getScoresFromServer()
                updateScoresList(scoresList)
            }

            // Escuchar actualizaciones de puntuaciones desde el servidor
            socket.on("scoreUpdate") { args ->
                runOnUiThread {
                    GlobalScope.launch(Dispatchers.Main) {
                        val updatedScoresList = args[0] as ArrayList<*>
                        val scoresList = ArrayList<Score>()

                        for (i in 0 until updatedScoresList.size) {
                            try {
                                val scoreObject = updatedScoresList[i] as org.json.JSONObject
                                val name = scoreObject.getString("name")
                                val score = scoreObject.getInt("score")
                                val scoreItem = Score(name, score)
                                scoresList.add(scoreItem)
                            } catch (e: JSONException) {
                                e.printStackTrace()
                                Log.e("Scoreboard", "Error al parsear el objeto JSON: ${e.message}")
                            }
                        }

                        updateScoresList(scoresList)
                    }
                }
            }
        }

        private fun updateScoresList(newList: List<Score>) {
            // Actualizar la lista en el adaptador y notificar los cambios
            scoresAdapter.submitList(newList)
            scoresAdapter.notifyDataSetChanged()
            Log.d("Scoreboard", "Lista de puntuaciones actualizada: $newList")
        }

        private suspend fun getScoresFromServer(): List<Score> {
            return withContext(Dispatchers.IO) {
                val url = "https://lezamapp-pvp-mode.glitch.me/scores"

                val jsonArrayRequest = JsonArrayRequest(
                    Request.Method.GET, url, null,
                    { response ->
                        val scoresList = ArrayList<Score>()

                        for (i in 0 until response.length()) {
                            val scoreObject = response.getJSONObject(i)
                            val name = scoreObject.getString("name")
                            val score = scoreObject.getInt("score")
                            val scoreItem = Score(name, score)
                            scoresList.add(scoreItem)
                        }

                        Log.d("Scoreboard", "Puntuaciones obtenidas correctamente: $scoresList")

                        // Emitir actualización a todos los clientes conectados
                        socket.emit("talk", "Actualización de puntuaciones")

                        // Actualizar la lista en el adaptador y notificar los cambios
                        updateScoresList(scoresList)

                    },
                    { error ->
                        Toast.makeText(this@Scoreboard, "Error al obtener puntuaciones: ${error.message}", Toast.LENGTH_SHORT).show()
                        Log.e("Scoreboard", "Error al obtener puntuaciones: ${error.message}")
                    }
                )

                val queue = Volley.newRequestQueue(this@Scoreboard)
                queue.add(jsonArrayRequest)

                return@withContext ArrayList<Score>()
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            socket.disconnect()
        }
    }
*/
