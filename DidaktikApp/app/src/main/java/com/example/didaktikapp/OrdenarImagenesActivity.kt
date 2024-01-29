package com.example.didaktikapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

    private val repeatActivityMenu = RepeatActivityMenu(this)
    private var gameManagerService: GameManagerService? = GameManagerService()
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ordenar_imagenes)

        // Obtén una referencia al contenedor de fragmentos
        setupHeaderFragment(savedInstanceState)

        gameManagerService = GameManager.get()
        progressBar = findViewById(R.id.ordenarImagenesProgressBar)
        gameManagerService!!.setInitialProgress(progressBar)


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

    private fun setupHeaderFragment(savedInstanceState: Bundle?) {
        val fragmentContainer = findViewById<FrameLayout>(R.id.titleFragmentTag)
        if (savedInstanceState == null) {
            val titleFragment = TitleFragment.newInstance(resources.getString(R.string.infoTitle))
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

                                // Verificar si todas las imágenes están colocadas
                                if (checkAllImagesPlacedCorrectly()) {
                                    gameManagerService?.addProgress(progressBar)
                                    val intent = Intent(this, OrdenarImagenesActivity::class.java)
                                    repeatActivityMenu.showGameOverDialog(this, intent)
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

    private fun checkAllImagesPlacedCorrectly(): Boolean {
        // Verificar si todas las imágenes están colocadas correctamente
        return imagen1.visibility == View.INVISIBLE &&
                imagen2.visibility == View.INVISIBLE &&
                imagen3.visibility == View.INVISIBLE &&
                imagen4.visibility == View.INVISIBLE &&
                imagen5.visibility == View.INVISIBLE
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun onHomeButtonClicked() {
        // Acciones a realizar cuando se hace clic en el botón Home
        // Utiliza NavigationUtil para la navegación
        NavigationUtil.navigateToMainMenu(this)
    }
}
