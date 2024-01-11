package com.example.didaktikapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley

class Scoreboard : AppCompatActivity() {

    private lateinit var scoresAdapter: ScoresAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scoreboard)

        scoresAdapter = ScoresAdapter()

        val scoresRecyclerView: RecyclerView = findViewById(R.id.scoresRecyclerView)
        scoresRecyclerView.layoutManager = LinearLayoutManager(this)
        scoresRecyclerView.adapter = scoresAdapter

        getScoresFromServer()
    }

    private fun getScoresFromServer() {
        val queue = Volley.newRequestQueue(this)
        val url = "https://lezamapp-pvp-mode.glitch.me/scores" // Reemplaza con la URL de tu servidor

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

                scoresAdapter.submitList(scoresList)
            },
            { error ->
                Toast.makeText(this, "Error al obtener puntuaciones: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        queue.add(jsonArrayRequest)
    }
}
