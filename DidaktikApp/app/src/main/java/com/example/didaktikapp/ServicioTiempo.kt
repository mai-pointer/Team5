package com.example.didaktikapp

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.SystemClock

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

    fun Detener(): Long {
        val tiempo: Long = SystemClock.elapsedRealtime() - tiempoInicio
        stopSelf()
        return tiempo / 1000
    }

}
