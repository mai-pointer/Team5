package com.example.didaktikapp


import android.net.Uri
import android.os.Bundle
import android.widget.Button
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import androidx.appcompat.app.AppCompatActivity

class Video : AppCompatActivity() {

    private val videos = mapOf(
        "Juego3.4" to R.raw.videotrescuatro,
        "Juego5.4" to R.raw.videocincocuatro,
    )

    private lateinit var exoPlayer: SimpleExoPlayer
    private lateinit var playerView: PlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        playerView = findViewById(R.id.video)

        // Obtiene el identificador del recurso de video
        val videoResource = videos[GameManager.get()?.pantallaActual()]!!

        // Especifica la ruta del video utilizando el identificador del recurso
        val videoUri = Uri.parse("android.resource://" + packageName + "/" + videoResource)

        // Configura ExoPlayer utilizando SimpleExoPlayer.Builder
        exoPlayer = SimpleExoPlayer.Builder(this)
            .setTrackSelector(DefaultTrackSelector(this))
            .build()

        // Asigna el reproductor al PlayerView
        playerView.player = exoPlayer

        val dataSourceFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, "TuApp"))
        val videoSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(videoUri)

        // Prepara el reproductor con la fuente del video
        exoPlayer.setMediaSource(videoSource)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true

        // Botón para terminar el video
        findViewById<Button>(R.id.terminar_video).setOnClickListener {
            GameManager.get()?.nextScreen()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Libera los recursos de ExoPlayer
        exoPlayer.release()
    }
}
