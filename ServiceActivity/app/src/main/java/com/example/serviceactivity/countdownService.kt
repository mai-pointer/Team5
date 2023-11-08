package com.example.serviceactivity

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.*

interface CountdownListener {
    fun onTimeTick(timeRemaining: Long)
}
class countdownService : Service() {

    private var countdownListener: CountdownListener? = null

    private var mediaPlayer: MediaPlayer? = null
    private var job: Job? = null
    private var secondsRemaining: Long = 0
    private var isPaused = false
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences("MyServicePrefs", Context.MODE_PRIVATE)
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action


        if (action == ACTION_PAUSE) {
            pauseCountdown()
        } else {
            // Lógica para iniciar el contador aquí
            val timeInMillis = intent?.getLongExtra(TIME_EXTRA, 0) ?: 0

            if (START_FLAG_REDELIVERY != 0) {
                secondsRemaining = sharedPreferences.getLong("secondsRemaining", timeInMillis / 1000)
            } else {
                secondsRemaining = timeInMillis / 1000
            }

            job = CoroutineScope(Dispatchers.Main).launch {
                while (secondsRemaining > 0) {
                    if (!isPaused){
                        delay(1000)
                        countdownListener?.onTimeTick(secondsRemaining)
                        secondsRemaining--
                        Log.d("CountdownService", "Seconds remaining: $secondsRemaining")
                    }
                    else{
                        delay(1000)
                    }
                }
                Log.d("CountdownService", "Countdown finished")
                playSound()
                stopSelf()
            }
        }

        return START_REDELIVER_INTENT
    }

    fun setCountdownListener(listener: CountdownListener) {
        countdownListener = listener
    }

    private fun playSound() {
        mediaPlayer = MediaPlayer.create(this, R.raw.sound)
        mediaPlayer?.start()
        mediaPlayer?.setOnCompletionListener {
            it.release()
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    fun pauseCountdown() {
        onDestroy()
    }



    override fun onDestroy() {
        job?.cancel()
        mediaPlayer?.release()

        if(secondsRemaining <= 0){
            val editor = sharedPreferences.edit()
            editor.remove("secondsRemaining")
            editor.apply()
        } else{
            val editor = sharedPreferences.edit()
            editor.putLong("secondsRemaining", secondsRemaining)
            editor.apply()

        }


        super.onDestroy()
    }

    companion object {
        const val TIME_EXTRA = "time_extra"
        const val ACTION_PAUSE = "com.example.serviceactivity.PAUSE"
    }

}