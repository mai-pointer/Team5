package com.example.didaktikapp

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder

class GameManager : Service() {

    // Variables para almacenar el estado del juego y la posición actual en las pantallas.
    private lateinit var context: Context
    private var juegoActual: Game? = null
    private var pantallaActual = 0

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    // Método para iniciar un nuevo juego.
    private fun startGame(gameName: String) {
        juegoActual = GamesRepository.games.firstOrNull { it.name == gameName }
        pantallaActual = 0
        pantalla()
    }

    // Método para avanzar a la siguiente pantalla en el juego.
    private fun nextScreen() {
        // Verifica si hay más pantallas en el juego.
        if (pantallaActual < (juegoActual?.screenOrder?.size ?: 0) - 1) {
            // Avanza a la siguiente pantalla.
            pantallaActual++
            pantalla()
        } else {
            // Fin del juego, puedes reiniciar o realizar alguna acción.
            // En este ejemplo, simplemente se comenta que se ha alcanzado el final del juego.
        }
    }

    // Método privado para navegar a la pantalla actual del juego.
    private fun pantalla() {
        juegoActual?.let {
            juegoActual?.let {
                // Obtiene la clase de la pantalla actual y crea un Intent para iniciar la pantalla.
                val screenClass = it.screenOrder[pantallaActual]
                val screenIntent = Intent(context, screenClass)
                screenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(screenIntent)
            }
        }
    }
}

data class Game(val name: String, val screenOrder: List<Class<*>>)

object GamesRepository {
    val games = listOf(
        Game("Juego1", listOf(
            Info::class.java,
            Crucigrama::class.java,
        )),
    )
}

//GameManager.instance.startGame("Juego1", applicationContext)
//GameManager.instance.nextScreen() // Avanzar a la siguiente pantalla

