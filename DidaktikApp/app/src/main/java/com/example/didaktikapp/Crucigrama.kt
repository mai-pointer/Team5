package com.example.didaktikapp

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class Crucigrama : AppCompatActivity() {

    //VARIABLES PARA LA PLANTILLA Y LAS PISTAS
    val pistas = arrayListOf(
        Pista(this, "Zerbait handiagoa egin"),
        Pista(this, "Athletic Clubaren zelaiaren izena"),
        Pista(this, "Atzean daukazuena, futbol..."),
        Pista(this, "San Mamesetik mugitu zena eta orain hemen dagoena"),
        Pista(this, "Arkua dagoen herriaren izena"),
    )
    val plantilla = """
            [       |         |       |         |       |       | H , 1   ]
            [       |         |       |         |       |       | A , 1   ]
            [       | S , 2   |       |         |       |       | N , 1   ]
            [       | A , 2   |       | Z , 3   |       |       | D , 1   ]
            [       | N , 2   |       | E , 3   |       |       | I , 1   ]
            [       | M , 2   |       | L , 3   |       |       | T , 1   ]
            [       | A , 2   |       | A , 4,3 | R , 4 | K , 4 | U , 1,4 ]
            [       | M , 2   |       | I , 3   |       |       |         ]
            [ L , 5 | E , 5,2 | Z , 5 | A , 3,5 | M , 5 | A , 5 |         ]
            [       | S , 2   |       |         |       |       |         ]
        """
    //OTRAS
    var pista_actual = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crucigrama)

        //VARIABLES
        val crucigrama: List<List<Celda>> = Crear(plantilla.trimIndent())
        val grid = findViewById<androidx.gridlayout.widget.GridLayout>(R.id.crucigrama)


        // CREAR EL CRUCIGRAMA
        for ((filaIndex, fila) in crucigrama.withIndex()) {
            val params = GridLayout.LayoutParams()
            params.rowSpec = GridLayout.spec(filaIndex)

            // Ajustar el número de columnas en el GridLayout para esta fila
            grid.setColumnCount(fila.size)

            for ((columnaIndex, celda) in fila.withIndex()) {
                // Añadir una vista a la cuadrícula
                params.columnSpec = GridLayout.spec(columnaIndex)

                val cellSize = resources.getDimensionPixelSize(R.dimen.cell_size)
                params.setMargins(2, 1, 2, 1)
                params.width = cellSize
                params.height = cellSize
                params.setGravity(Gravity.FILL)

                if (celda.caracter != ' ') {
                    val text = Text1()
                    text.layoutParams = params
                    grid.addView(text)

                    // Crear las pistas
                    if (celda.pista >= 0){
                        pistas[celda.pista-1].casillas.add(Pista.Casilla(celda.caracter, text))
                    }
                    if (celda.pista_secundaria >= 0){
                        pistas[celda.pista_secundaria-1].casillas.add(Pista.Casilla(celda.caracter, text))
                    }

                } else {
                    val text = Text2()
                    text.layoutParams = params
                    grid.addView(text)
                }
            }
        }

        //CREAR LAS PISTAS
        for ((index, pista) in pistas.withIndex()){
            pista.Activar(index)
        }

        Pistas(0)

        findViewById<ImageButton>(R.id.mas_pista).setOnClickListener{
            Pistas(1)
        }
        findViewById<ImageButton>(R.id.menos_pista).setOnClickListener{
            Pistas(-1)
        }

        //TERMINAR PARTIDA
        findViewById<Button>(R.id.terminar_crucigrama).setOnClickListener{
            //Comprueba si esta bien
            var bien = true
            for (pista in pistas){
                //Corrigiendo cada pista
                for (casilla in pista.casillas){
                    if (!casilla.editText.text.toString().isNotBlank()){
                        bien = false
                        break;
                    }
                    if (casilla.editText.text.toString().uppercase()!= casilla.caracter.toString().uppercase()){
                        bien = false
                        Toast.makeText(this, "¡Has fallado!", Toast.LENGTH_SHORT).show()
                        casilla.editText.setTextColor(Color.RED)
                    }
                }
            }
            if (bien){
                Toast.makeText(this, "¡Terminado!", Toast.LENGTH_SHORT).show()
            }

        }
    }

    fun Pistas(cambio: Int, literal: Boolean = false){

        //Cambia el color
        CambiarColor(Color.BLACK)
        //Desactiva la pista
        pistas[pista_actual].activa = false

        // Cambiar la pista actual
        if (!literal){
            var nueva = cambio + pista_actual
            if (nueva < 0) nueva = pistas.size - 1
            if (nueva >= pistas.size) nueva = 0

            pista_actual = nueva
        } else {
            pista_actual = cambio
        }

        //Cambiar el texto de la pista
        findViewById<TextView>(R.id.pista).text = pistas[pista_actual].texto

        //Cambia el colors
        CambiarColor(ContextCompat.getColor(this, R.color.verdeOscuro))
        //Activa la pista
        pistas[pista_actual].activa = true
    }

    fun CambiarColor(color: Int) {
        //Cambiar el color de la pista
        for (casilla in pistas[pista_actual].casillas) {
            val border = GradientDrawable()
            border.setColor(Color.WHITE)
            border.setStroke(4, color)
            border.cornerRadius = 8f

            casilla.editText.background = border
        }
    }

    fun Text1(): EditText {
        val editText = EditText(this)
        editText.setPadding(1, 1, 1, 1)
        editText.textSize = 14f
        editText.gravity = Gravity.CENTER
        editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
//        editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(1))

        // Configurar bordes redondeados y más gruesos
        val border = GradientDrawable()
        border.setColor(Color.WHITE) // Fondo blanco
        border.setStroke(4, Color.BLACK) // Grosor y color del borde
        border.cornerRadius = 8f // Radio del borde, ajusta según sea necesario

        editText.background = border

        return editText
    }

    fun Text2(): TextView {
        val textView = TextView(this)
        textView.text = " "
        textView.gravity = Gravity.CENTER

        return textView
    }

    // Función para crear un crucigrama a partir de una plantilla en formato de cadena
    fun Crear(plantilla: String): List<List<Celda>> {
        return plantilla.lines().filter { fila ->                       // Lo divide en lineas
            fila.isNotBlank() }.map { fila ->                           // Elimina las lineas en blanco
                fila.trim()
                    .substring(1, fila.length - 1)                      // Elimina los corchetes al principio y al final
                    .split("|")                              // Divide la fila en columnas apartir de las comas
                    .map { columna ->
                        val elementos = columna.trim().split(',')
                        val (caracter, pista, pista_secundaria) =
                            if (elementos.size >= 3) elementos
                            else if (elementos.size == 2) elementos + listOf("-1")
                            else listOf("", "-1", "-1")
                        Celda(caracter.firstOrNull() ?: ' ', pista.trim().toIntOrNull() ?: -1, pista_secundaria.trim().toIntOrNull() ?: -1)
                    }
        }
    }

    data class Celda(val caracter: Char, val pista: Int, val pista_secundaria: Int) {
        var caracterJugador: Char = ' '
    }

    data class Pista(val context: Crucigrama ,val texto: String){
        var casillas = mutableListOf<Casilla>()
        var activa = false
        var numero = -1

        fun Activar(numero: Int) {
            this.numero = numero

            for ((index, casilla) in casillas.withIndex()) {

                //Cuando se le pone el foco al EditText
                casilla.editText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        // Se activa solo cuando se le da el foco, no cuando se lo quita
                        if (casilla.editText.tag != "autoFocus") {
                            context.Pistas(numero, true)
                        }
                    } else {
                        // Aquí es donde puedes quitar el tag una vez que ya no lo usas
                        casilla.editText.tag = null
                    }
                }

                //Cuando se escribe en el EditText
                casilla.editText.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
                    override fun afterTextChanged(s: Editable?) {
                        //Pone el color original
                        casilla.editText.setTextColor(Color.BLACK)

                        // Limitar el número de caracteres a 1
                        if (s?.length ?: 0 > 1) {
                            casilla.editText.setText(s?.subSequence(0, 1))
                            casilla.editText.setSelection(1) // Mover el cursor al final del texto
                        }

                        // Cambiar el foco al siguiente EditText si hay caracteres escritos
                        if (!s.isNullOrBlank() && activa) {
                            val nextIndex = index + 1
                            if (nextIndex < casillas.size) {
                                //Salta al siguiente EditText
                                casillas[nextIndex].editText.tag = "autoFocus"
                                casillas[nextIndex].editText.requestFocus()
                            }
                        }
                    }
                })
            }
        }

        data class Casilla(val caracter: Char, val editText: EditText)
    }
}