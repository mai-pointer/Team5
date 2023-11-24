package com.example.didaktikapp

import android.os.Bundle
import android.widget.GridLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


class PuzzleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puzzle)

        // Referencias a las vistas en el layout
        val imageViewFullscreen = findViewById<ImageView>(R.id.imageViewFullscreen)
        val gridLayoutPuzzle = findViewById<GridLayout>(R.id.gridLayoutPuzzle)

        // Establecer la imagen de fondo
        imageViewFullscreen.setImageResource(R.drawable.baseline_church_24) // Reemplaza con el recurso de tu imagen

        // Crear cuadrícula estilo puzle
        for (i in 0 until gridLayoutPuzzle.childCount) {
            val puzzlePiece = gridLayoutPuzzle.getChildAt(i) as ImageView
            puzzlePiece.layoutParams = GridLayout.LayoutParams()
            puzzlePiece.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    android.R.color.darker_gray
                )
            )
        }

        // Puedes agregar la lógica del puzle aquí, como manejar eventos de toque y reorganizar las piezas.
    }
}
