package com.example.didaktikapp

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Button

class Info : AppCompatActivity() {

    val informacion = hashMapOf<Double, Informacion>(
        1.2 to Informacion(
            "Une honetan, Lezamako nekazaritza-kooperatibaren ondoan gaude. Bertan, urtez urte herriko idi-probak egiten dira. Lezamako kasuan, proba hauek urtero egiten dira jaietan. Normalean, idiek 500 eta 650 kg arteko pisua izaten dute. Proba edo apustu garrantzitsuen aurreko egunetan, idiei lana murriztu eta elikadura-erregimen berezi bat ezartzen zaie; erregimen horren oinarria babak dira.",
            R.raw.unopuntodos
        )
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

//        intent.getDoubleExtra("punto", 0.0)

        findViewById<TextView>(R.id.info).text = informacion[1.2]?.texto
        findViewById<Button>(R.id.terminar_info).setOnClickListener{
            GameManager.get()?.nextScreen()
        }
    }

    data class Informacion(val texto: String, val audio: Int? = null)
}