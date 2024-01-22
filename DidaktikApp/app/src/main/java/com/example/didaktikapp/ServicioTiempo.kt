package com.example.didaktikapp

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.SystemClock
import java.util.Locale

class ServicioTiempo : Service() {

    private val binder: IBinder = LocalBinder()
    private var tiempoInicio: Long = 0

    inner class LocalBinder : Binder() {
        fun getService(): ServicioTiempo = this@ServicioTiempo
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        tiempoInicio = SystemClock.elapsedRealtime()
        return START_STICKY
    }

    fun Detener(): String {
        val tiempo : Long = SystemClock.elapsedRealtime() - tiempoInicio
        stopSelf()
        return Formatear(tiempo)
    }

    private fun Formatear(elapsedTime: Long): String {
        val hours = elapsedTime / 3600000
        val minutes = (elapsedTime % 3600000) / 60000
        val seconds = (elapsedTime % 60000) / 1000
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    }
}
