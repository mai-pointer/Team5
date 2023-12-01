package com.example.didaktikapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.widget.EditText
import androidx.gridlayout.widget.GridLayout

class Crucigrama : AppCompatActivity() {

    val gridX = 10
    val gridY = 10
    val palabras: List<Palabra> = listOf(
        Palabra(this, "COCHE", "Medio de transporte", 0, 1, true),
        Palabra(this, "COCHE", "Medio de transporte", 1, 1, true),
        Palabra(this, "COCHE", "Medio de transporte", 2, 1, true),
        Palabra(this, "COCHE", "Medio de transporte", 3, 1, true),
        Palabra(this, "COCHE", "Medio de transporte", 4, 1, true),
        Palabra(this, "COCHE", "Medio de transporte", 5, 1, true),
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crucigrama)

        //Crea el array de celdas
        val celdas = Array(gridX) {
            Array(gridY) {
                Celda()
            }
        }

        //Comprueba si alguna palabra tiene una letra en esa posición
        for ((indice, palabra) in palabras.withIndex()) {
            //Añade la palabra a la celda
            for (elemento in palabra.elementos) {
                celdas[elemento.x][elemento.y].caracter = elemento.caracter
                celdas[elemento.x][elemento.y].posicion_palabra = indice
            }

            //AVISA SI SALE DEL GRID O SI SE JUNTAN DOS CARACTERES QUE NO SON IGUALES***
        }

        //Crea el grid layout
        val crucigrama = findViewById<GridLayout>(R.id.crucigrama)

        for (i in celdas.indices) {
            for (j in celdas[i].indices) {
                val celda = celdas[i][j]
                Log.i("CRUCIGRAMA", "Celda $i $j: ${celda.caracter}")
                ///EN CADA CELDA AÑADE O UN EDIT TEXT O UN ESPACIO SI EL CARACTER ES UN ESPACIO***
            }
        }
    }

    //Clase para guardar cada elemento de la celda
    class Celda{
        var caracter: Char = ' '
        var posicion_palabra: Int = 0
    }

    //Clase que contiene la info de la palabra
    class Palabra(
        //Valores de la palabra
        val context: Context,
        val palabra: String,
        val pista: String,
        val x: Int,
        val y: Int,
        val horizontal: Boolean
    ){
        //Info de cada letra
        class Elemento(val context: Context, val x: Int, val y: Int){
            val caracter = ' '
        }
        var editText: List<EditText> = listOf()
        var elementos: List<Elemento> = listOf()

        //Inicializa la info de cada letra
        init {
            var cont : Int = 0
            var elemento = Elemento(context, x, y)

            for (c in palabra.split("")){
                elemento.caracter.plus(c)
                if(horizontal){
                    elemento.x.plus(x + cont)
                    elemento.y.plus(y)
                } else{
                    elemento.x.plus(x)
                    elemento.y.plus(y + cont)
                }
                cont += 1;
            }
        }
    }
}