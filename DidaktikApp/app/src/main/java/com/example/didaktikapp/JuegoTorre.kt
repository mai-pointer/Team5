package com.example.didaktikapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.component2
import androidx.viewpager2.widget.ViewPager2
import com.example.didaktikapp.navigation.NavigationUtil
import com.example.didaktikapp.titleFragment.TitleFragment
import com.example.didaktikapp.ImageAdapter

class JuegoTorre : AppCompatActivity() {
    private val repeatActivityMenu = RepeatActivityMenu(this)

    private var draggedImagePosition: Int? = null
    private lateinit var imageAdapter: ImageAdapter

    var images = mutableListOf(
        R.drawable.ahatea to "ahatea",
        R.drawable.armairua to "armairua",
        R.drawable.behia to "behia",
        R.drawable.leihoa01 to "leihoa01",
        R.drawable.leihoa02 to "leihoa02",
        R.drawable.leihoa03 to "leihoa03",
        R.drawable.mahaia to "mahaia",
        R.drawable.ohea to "ohea",
        R.drawable.oiloa to "oiloa",
        R.drawable.txerria to "txerria",
        R.drawable.tximinia to "tximinia",
        R.drawable.zaldia to "zaldia"
    )

    private lateinit var viewPager: ViewPager2
    private lateinit var hueco1: RelativeLayout
    private lateinit var hueco2: RelativeLayout
    private lateinit var hueco3: RelativeLayout
    private lateinit var btnPrevious: ImageButton
    private lateinit var btnNext: ImageButton
    private val imagesPlacedCorrectly = mutableSetOf<Int>()

    private var draggedImageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_juego_torre)


        // Configura el ViewPager2
        viewPager = findViewById(R.id.viewPager)
        btnPrevious = findViewById(R.id.btnPrevious)
        btnNext = findViewById(R.id.btnNext)

        // Usa la variable images para crear el imageAdapter
        loadCarrousel()


        // Configura los listeners para los botones
        btnPrevious.setOnClickListener { showPreviousImage() }
        btnNext.setOnClickListener { showNextImage() }

        // Obtén referencias a los huecos
        hueco1 = findViewById(R.id.batSolairua)
        hueco2 = findViewById(R.id.biSolairua)
        hueco3 = findViewById(R.id.hiruSolairua)

        // Configura el escuchador de arrastre para los huecos
        setDragListener(hueco1, 0)
        setDragListener(hueco2, 1)
        setDragListener(hueco3, 2)

        // Obtén una referencia al contenedor de fragmentos
        val fragmentContainer = findViewById<FrameLayout>(R.id.fragmentContainerView)

        if (savedInstanceState == null) {
            val titleFragment = TitleFragment.newInstance("Dorrea")
            supportFragmentManager.beginTransaction()
                .replace(fragmentContainer.id, titleFragment, "titleFragmentTag").commit()
        }

        // Configura el click listener para el botón en el fragmento
        val titleFragment =
            supportFragmentManager.findFragmentByTag("titleFragmentTag") as TitleFragment?
        titleFragment?.setOnHomeButtonClickListener(View.OnClickListener {
            onHomeButtonClicked()
        })
    }

    fun showPreviousImage() {
        val currentItem = viewPager.currentItem
        if (currentItem > 0) {
            viewPager.setCurrentItem(currentItem - 1, true)
        }
    }

    fun showNextImage() {
        val currentItem = viewPager.currentItem
        if (currentItem < imageAdapter.itemCount - 1) {
            viewPager.setCurrentItem(currentItem + 1, true)
        }
    }

    private fun setDragListener(hueco: RelativeLayout, position: Int) {
        hueco.setOnDragListener { v, event ->
            when (event.action) {


                DragEvent.ACTION_DROP -> {
                    draggedImageView = event.localState as? ImageView
                    val draggedImageIdentifier = draggedImageView?.tag.toString()
                    val myPicPos =
                        images.indexOfFirst { it.second == draggedImageView?.tag.toString() }
                    //draggedImagePosition = myPicPos

                    // Restablecer el color de fondo del hueco a su estado original
                    hueco.setBackgroundColor(Color.TRANSPARENT)

                    // Aquí deberías comparar con los identificadores esperados y cambiar el color
                    if (!draggedImageIdentifier.isNullOrEmpty()) {
                        if (checkImagePlacement(draggedImageIdentifier, hueco)) {

                            // Desaparecer la ImageView del ViewPager2 solo si la imagen es correcta
                            draggedImageView?.visibility = View.INVISIBLE

                            // Eliminar la imagen del array y notificar al adaptador
                            imageAdapter.removeImage(myPicPos)
                            loadCarrousel()

                            // Verificar si todas las imágenes están colocadas correctamente
                            if (images.isEmpty()) {
                                // Implementa las acciones finales del juego aquí
                                showRestartDialog()

                            }
                        } else {

                            // Si la imagen no es correcta, puedes mantenerla visible
                            draggedImageView?.visibility = View.VISIBLE
                        }
                    }

                    true
                }

                else -> true
            }
        }
    }

    private fun checkImagePlacement(
        draggedImageIdentifier: String, hueco: RelativeLayout
    ): Boolean {
        // Comparar el identificador de la imagen con los identificadores esperados
        when {
            draggedImageIdentifier == "leihoa01" && hueco == hueco3 -> {
                findViewById<ImageView>(R.id.leihoa01).visibility = View.VISIBLE
                return true
            }

            draggedImageIdentifier == "armairua" && hueco == hueco2 -> {
                findViewById<ImageView>(R.id.armairua).visibility = View.VISIBLE
                return true
            }

            draggedImageIdentifier == "leihoa02" && hueco == hueco3 -> {
                findViewById<ImageView>(R.id.leihoa02).visibility = View.VISIBLE
                return true
            }

            draggedImageIdentifier == "leihoa03" && hueco == hueco3 -> {
                findViewById<ImageView>(R.id.leihoa03).visibility = View.VISIBLE
                return true
            }

            draggedImageIdentifier == "ahatea" && hueco == hueco1 -> {
                findViewById<ImageView>(R.id.ahatea).visibility = View.VISIBLE
                return true
            }

            draggedImageIdentifier == "behia" && hueco == hueco1 -> {
                findViewById<ImageView>(R.id.behia).visibility = View.VISIBLE
                return true
            }

            draggedImageIdentifier == "mahaia" && hueco == hueco2 -> {
                findViewById<ImageView>(R.id.mahaia).visibility = View.VISIBLE
                return true
            }

            draggedImageIdentifier == "ohea" && hueco == hueco2 -> {
                findViewById<ImageView>(R.id.ohea).visibility = View.VISIBLE
                return true
            }

            draggedImageIdentifier == "oiloa" && hueco == hueco1 -> {
                findViewById<ImageView>(R.id.oiloa).visibility = View.VISIBLE
                return true
            }

            draggedImageIdentifier == "txerria" && hueco == hueco1 -> {
                findViewById<ImageView>(R.id.txerria).visibility = View.VISIBLE
                return true
            }

            draggedImageIdentifier == "tximinia" && hueco == hueco2 -> {
                findViewById<ImageView>(R.id.tximinia).visibility = View.VISIBLE
                return true
            }

            draggedImageIdentifier == "zaldia" && hueco == hueco1 -> {
                findViewById<ImageView>(R.id.zaldia).visibility = View.VISIBLE
                return true
            }

            else -> {
                return false
            }
        }
    }


    private fun getHuecoForImage(index: Int): RelativeLayout {
        // Devuelve el hueco asociado al índice de la imagen
        return when (index) {
            0 -> hueco1
            1 -> hueco2
            2 -> hueco3
            else -> hueco1
        }
    }

    private fun onHomeButtonClicked() {
        // Acciones a realizar cuando se hace clic en el botón Home
        // Utiliza NavigationUtil para la navegación
        NavigationUtil.navigateToMainMenu(this)
    }

    fun loadCarrousel() {
        imageAdapter = ImageAdapter(images)
        viewPager.adapter = imageAdapter
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun showRestartDialog() {
        val intent = Intent(this ,Crucigrama::class.java)
        repeatActivityMenu.showGameOverDialog(this, intent)
    }
}

