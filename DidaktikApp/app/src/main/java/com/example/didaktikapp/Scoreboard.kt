package com.example.didaktikapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONArray
import org.json.JSONObject

// Asegúrate de agregar la dependencia en tu archivo build.gradle
// implementation("io.socket.client:socket.io-client:1.0.1")

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
