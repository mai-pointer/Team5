package com.example.didaktikapp

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class DiferenciasActivity : AppCompatActivity() {
    lateinit var botones: List<Button>
    lateinit var diferencias: List<ConstraintLayout>

    var contDifs = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diferencias)


        botones = listOf(
            findViewById(R.id.btnDife6),
            findViewById(R.id.btnDife5),
            findViewById(R.id.btnDife4),
            findViewById(R.id.btnDife3),
            findViewById(R.id.btnDife2),
            findViewById(R.id.btnDife1)
        )

        diferencias = listOf(
            findViewById(R.id.dife6),
            findViewById(R.id.dife5),
            findViewById(R.id.dife4),
            findViewById(R.id.dife3),
            findViewById(R.id.dife2),
            findViewById(R.id.dife1)
        )


        ocultarDiferencias()

        for (i in botones.indices) {
            botones[i].setOnClickListener {
                diferencias[i].background.alpha = 255
                botones[i].isEnabled = false
                actualizarContador()
            }
        }
    }

    private fun ocultarDiferencias() {
        for (diferencia in diferencias) {
            diferencia.background.alpha = 0
        }
    }

    private fun actualizarContador() {
        var btnCont = findViewById<Button>(R.id.contDifs)
        var imgTouch = findViewById<ImageView>(R.id.clickimg)

        if (contDifs >=6) {
            //Ganaste!
        }
        else {
            contDifs++
            btnCont.text = contDifs.toString()
            if (contDifs == 1){
                imgTouch.visibility = ImageView.INVISIBLE
            }
        }
    }
}