package com.example.serviceactivity

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log

class countdownService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var countDownTimer: CountDownTimer? = null
    private var secondsRemaining: Long = 0

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val timeInMillis = intent?.getLongExtra(TIME_EXTRA, 0) ?: 0
        secondsRemaining = timeInMillis / 1000

        countDownTimer = object : CountDownTimer(timeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000
                Log.d("MyService", "Seconds remaining: $secondsRemaining")
            }

            override fun onFinish() {
                Log.d("MyService", "Countdown finished")
                playSound()
                stopSelf()
            }
        }

        countDownTimer?.start()
        return START_STICKY
    }

    private fun playSound() {
        mediaPlayer = MediaPlayer.create(this, R.raw.sound) // Reemplaza con tu archivo de sonido
        mediaPlayer?.start()
        mediaPlayer?.setOnCompletionListener {
            it.release()
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        countDownTimer?.cancel()
        mediaPlayer?.release()
        super.onDestroy()
    }

    companion object {
        const val TIME_EXTRA = "time_extra"
    }

}