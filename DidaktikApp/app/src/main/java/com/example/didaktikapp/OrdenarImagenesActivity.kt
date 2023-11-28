package com.example.didaktikapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.didaktikapp.R
import com.example.didaktikapp.titleFragment.TitleFragment

class OrdenarImagenesActivity : AppCompatActivity() {

    private lateinit var imagen1: ImageView
    private lateinit var hueco1: RelativeLayout

    private lateinit var imagen2: ImageView
    private lateinit var hueco2: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ordenar_imagenes)

        // Obtén una referencia al TitleFragment
        val titleFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as TitleFragment

        // Configura el click listener para el botón en el fragmento
        titleFragment.setOnHomeButtonClickListener(View.OnClickListener {
            // Llama a la función onHomeButtonClicked del TitleFragment
            titleFragment.onHomeButtonClicked()
        })

        // Configura el título del fragmento
        titleFragment.setTitle("Ordenar Imágenes")

        imagen1 = findViewById(R.id.imagen1)
        hueco1 = findViewById(R.id.hueco1) as RelativeLayout  // Cambiado a RelativeLayout

        imagen2 = findViewById(R.id.imagen2)
        hueco2 = findViewById(R.id.hueco2) as RelativeLayout  // Cambiado a RelativeLayout

        // Configurar el escuchador de arrastre para las imágenes arrastrables
        setDragListener(imagen1, hueco1)
        setDragListener(imagen2, hueco2)
    }

    private fun setDragListener(
        imagen: ImageView,
        hueco: RelativeLayout
    ) {  // Cambiado a RelativeLayout
        imagen.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                val data = View.DragShadowBuilder(view)
                view.startDragAndDrop(null, data, view, 0)
                true
            } else {
                false
            }
        }

        hueco.setOnDragListener { v, event ->
            when (event.action) {
                DragEvent.ACTION_DROP -> {
                    val draggedView = event.localState
                    if (draggedView is ImageView) {
                        val dropTarget = v.tag as? String

                        if (!dropTarget.isNullOrEmpty() && draggedView.tag is String) {
                            val draggedTag = draggedView.tag as String

                            if (dropTarget == draggedTag) {
                                showToast("Imagen colocada correctamente en el hueco.")
                                draggedView.visibility = View.INVISIBLE
                                hueco.setBackgroundColor(Color.GREEN)

                                // Verificar si todas las imágenes están colocadas
                                if (imagen1.visibility == View.INVISIBLE && imagen2.visibility == View.INVISIBLE) {
                                    showToast("¡Juego completado!")
                                }
                            } else {
                                showToast("La imagen no es la correcta para este hueco.")
                                hueco.setBackgroundColor(Color.RED)
                            }
                        } else {
                            showToast("Error: No se pudo determinar la ubicación correcta de la imagen.")
                        }
                    } else {
                        showToast("Error: No se pudo obtener la vista arrastrada.")
                    }

                    true
                }

                else -> true
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    fun onHomeButtonClicked(view: View) {
        // Acciones a realizar cuando se hace clic en el botón Home
        // Por ejemplo, transición a MainMenuActivity
        val intent = Intent(this, MainMenuActivity::class.java)
        startActivity(intent)
    }
}
