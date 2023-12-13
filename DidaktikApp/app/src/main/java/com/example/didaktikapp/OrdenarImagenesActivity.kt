package com.example.didaktikapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.didaktikapp.R
import com.example.didaktikapp.navigation.NavigationUtil
import com.example.didaktikapp.titleFragment.TitleFragment

class OrdenarImagenesActivity : AppCompatActivity() {

    private lateinit var imagen1: ImageView
    private lateinit var hueco1: RelativeLayout
    private lateinit var imagen2: ImageView
    private lateinit var hueco2: RelativeLayout
    private lateinit var imagen3: ImageView
    private lateinit var hueco3: RelativeLayout
    private lateinit var imagen4: ImageView
    private lateinit var hueco4: RelativeLayout
    private lateinit var imagen5: ImageView
    private lateinit var hueco5: RelativeLayout

    private var imagesPlacedCorrectly = 0
    private val totalImages = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ordenar_imagenes)

        // Obtén una referencia al contenedor de fragmentos
        val fragmentContainer = findViewById<FrameLayout>(R.id.fragmentContainerView)

        // Reemplaza el contenedor con el TitleFragment
        if (savedInstanceState == null) {
            val titleFragment = TitleFragment.newInstance("Ordenar Imágenes")
            supportFragmentManager.beginTransaction()
                .replace(fragmentContainer.id, titleFragment, "titleFragmentTag")
                .commit()
        }

        // Configura el click listener para el botón en el fragmento
        val titleFragment =
            supportFragmentManager.findFragmentByTag("titleFragmentTag") as TitleFragment?
        titleFragment?.setOnHomeButtonClickListener(View.OnClickListener {
            onHomeButtonClicked()
        })

        imagen1 = findViewById(R.id.imagen1)
        hueco1 = findViewById(R.id.hueco1) as RelativeLayout

        imagen2 = findViewById(R.id.imagen2)
        hueco2 = findViewById(R.id.hueco2) as RelativeLayout

        imagen3 = findViewById(R.id.imagen3)
        hueco3 = findViewById(R.id.hueco3) as RelativeLayout

        imagen4 = findViewById(R.id.imagen4)
        hueco4 = findViewById(R.id.hueco4) as RelativeLayout

        imagen5 = findViewById(R.id.imagen5)
        hueco5 = findViewById(R.id.hueco5) as RelativeLayout

        // Configurar el escuchador de arrastre para las imágenes arrastrables
        setDragListener(imagen1, hueco1)
        setDragListener(imagen2, hueco2)
        setDragListener(imagen3, hueco3)
        setDragListener(imagen4, hueco4)
        setDragListener(imagen5, hueco5)
    }

    private fun setDragListener(
        imagen: ImageView,
        hueco: RelativeLayout
    ) {
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
                        val dropTarget = v.tag?.toString()

                        if (!dropTarget.isNullOrEmpty() && draggedView.tag is String) {
                            val draggedTag = draggedView.tag?.toString()

                            if (dropTarget == draggedTag) {
                                showToast("Imagen colocada correctamente en el hueco.")
                                draggedView.visibility = View.INVISIBLE
                                hueco.setBackgroundColor(Color.GREEN)

                                imagesPlacedCorrectly++

                                // Verificar si todas las imágenes están colocadas
                                if (imagesPlacedCorrectly == totalImages) {
                                    showGameOverDialog()
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

    private fun onHomeButtonClicked() {
        // Acciones a realizar cuando se hace clic en el botón Home
        // Utiliza NavigationUtil para la navegación
        NavigationUtil.navigateToMainMenu(this)
    }

    private fun showGameOverDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Jokoa amaituta")
        builder.setMessage("Berriro jolaztu nahi duzu?")
        builder.setPositiveButton("Bai") { _, _ ->
            // Si el usuario hace clic en "Bai" (Sí), reinicia la actividad
            restartActivity()
        }
        builder.setNegativeButton("Ez") { _, _ ->
            // Si el usuario hace clic en "Ez" (No), regresa al menú principal
            navigateToMainMenu()
        }

        // Mostrar el cuadro de diálogo
        builder.show()
    }

    private fun restartActivity() {
        // Reiniciar la actividad actual
        val intent = intent
        finish()
        startActivity(intent)
    }

    private fun navigateToMainMenu() {
        // Regresar al menú principal
        NavigationUtil.navigateToMainMenu(this)
    }
}
