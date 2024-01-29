package com.example.didaktikapp


import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ProgressBar
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import androidx.appcompat.app.AppCompatActivity
import com.example.didaktikapp.navigation.NavigationUtil
import com.example.didaktikapp.titleFragment.TitleFragment

class Video : AppCompatActivity() {

    private val videos = mapOf(
        "Juego3.4" to R.raw.videotrescuatro,
        "Juego5.4" to R.raw.videocincocuatro,
    )

    private lateinit var exoPlayer: SimpleExoPlayer
    private lateinit var playerView: PlayerView

    private var gameManagerService: GameManagerService? = GameManagerService()
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        playerView = findViewById(R.id.video)

        gameManagerService = GameManager.get()
        progressBar = findViewById(R.id.videoProgressBar)
        gameManagerService!!.setInitialProgress(progressBar)

        setupHeaderFragment(savedInstanceState)

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
            gameManagerService?.addProgress(progressBar)
            GameManager.get()?.nextScreen()
        }
    }

    private fun setupHeaderFragment(savedInstanceState: Bundle?) {
        val fragmentContainer = findViewById<FrameLayout>(R.id.fragmentContainerView)
        if (savedInstanceState == null) {
            val titleFragment = TitleFragment.newInstance(resources.getString(R.string.videoTitle))
            supportFragmentManager.beginTransaction()
                .replace(fragmentContainer.id, titleFragment, "titleFragmentTag")
                .commit()
        }
        val titleFragment =
            supportFragmentManager.findFragmentByTag("titleFragmentTag") as TitleFragment?
        titleFragment?.setOnHomeButtonClickListener {
            onHomeButtonClicked()
        }
    }

    private fun onHomeButtonClicked() {
        NavigationUtil.navigateToMainMenu(this)
    }
    override fun onDestroy() {
        super.onDestroy()
        // Libera los recursos de ExoPlayer
        exoPlayer.release()
    }
}
