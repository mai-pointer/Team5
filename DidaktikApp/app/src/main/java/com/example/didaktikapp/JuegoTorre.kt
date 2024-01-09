package com.example.didaktikapp

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.didaktikapp.navigation.NavigationUtil
import com.example.didaktikapp.titleFragment.TitleFragment

class JuegoTorre : AppCompatActivity() {

    private var draggedImagePosition: Int? = null
    private lateinit var imageAdapter: ImageAdapter


    private val images = listOf(
        R.drawable.ahatea,
        R.drawable.armairua,
        R.drawable.behia,
        R.drawable.leihoa01,
        R.drawable.leihoa02,
        R.drawable.leihoa03,
        R.drawable.mahaia,
        R.drawable.ohea,
        R.drawable.oiloa,
        R.drawable.txerria,
        R.drawable.tximinia,
        R.drawable.zaldia
    )

    private lateinit var viewPager: ViewPager2
    private lateinit var hueco1: RelativeLayout
    private lateinit var hueco2: RelativeLayout
    private lateinit var hueco3: RelativeLayout
    private lateinit var btnPrevious: ImageButton
    private lateinit var btnNext: ImageButton

    private var draggedImageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_juego_torre)

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

        // Configura el ViewPager2
        viewPager = findViewById(R.id.viewPager)
        btnPrevious = findViewById(R.id.btnPrevious)
        btnNext = findViewById(R.id.btnNext)

        // Usa la variable images para crear el imageAdapter
        imageAdapter = ImageAdapter(images)
        viewPager.adapter = imageAdapter

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
                .replace(fragmentContainer.id, titleFragment, "titleFragmentTag")
                .commit()
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
                DragEvent.ACTION_DRAG_STARTED -> {
                    // Guardar la referencia a la ImageView y su posición cuando comienza el arrastre
                    draggedImageView = event.localState as? ImageView
                    draggedImagePosition = position
                    true
                }

                DragEvent.ACTION_DROP -> {
                    val draggedView = event.localState as ImageView
                    val draggedImageIdentifier = draggedView.tag?.toString()

                    // Restablecer el color de fondo del hueco a su estado original
                    hueco.setBackgroundColor(Color.TRANSPARENT)

                    // Aquí deberías comparar con los identificadores esperados y cambiar el color
                    if (!draggedImageIdentifier.isNullOrEmpty()) {
                        if (checkImagePlacement(draggedImageIdentifier, hueco)) {

                            // Desaparecer la ImageView del ViewPager2 solo si la imagen es correcta
                            draggedImageView?.visibility = View.INVISIBLE

                            // Eliminar la imagen del array y notificar al adaptador
                            draggedImagePosition?.let { imageAdapter.removeImage(it) }

                            // Verificar si todas las imágenes están colocadas correctamente
                            if (checkAllImagesPlacedCorrectly()) {
                                // Implementa las acciones finales del juego aquí
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
        draggedImageIdentifier: String,
        hueco: RelativeLayout
    ): Boolean {
        // Comparar el identificador de la imagen con los identificadores esperados y cambiar el color
        when {
            draggedImageIdentifier == "leihoa01" && hueco == hueco3 -> hueco.setBackgroundColor(
                Color.GREEN
            )

            draggedImageIdentifier == "armairua" && hueco == hueco2 -> hueco.setBackgroundColor(
                Color.GREEN
            )

            draggedImageIdentifier == "leihoa02" && hueco == hueco3 -> hueco.setBackgroundColor(
                Color.GREEN
            )

            draggedImageIdentifier == "leihoa03" && hueco == hueco3 -> hueco.setBackgroundColor(
                Color.GREEN
            )

            draggedImageIdentifier == "ahatea" && hueco == hueco1 -> hueco.setBackgroundColor(
                Color.GREEN
            )

            draggedImageIdentifier == "behia" && hueco == hueco1 -> hueco.setBackgroundColor(
                Color.GREEN
            )

            draggedImageIdentifier == "mahaia" && hueco == hueco2 -> hueco.setBackgroundColor(
                Color.GREEN
            )

            draggedImageIdentifier == "ohea" && hueco == hueco2 -> hueco.setBackgroundColor(
                Color.GREEN
            )

            draggedImageIdentifier == "oiloa" && hueco == hueco1 -> hueco.setBackgroundColor(
                Color.GREEN
            )

            draggedImageIdentifier == "txerria" && hueco == hueco1 -> hueco.setBackgroundColor(
                Color.GREEN
            )

            draggedImageIdentifier == "tximinia" && hueco == hueco2 -> hueco.setBackgroundColor(
                Color.GREEN
            )

            draggedImageIdentifier == "zaldia" && hueco == hueco1 -> hueco.setBackgroundColor(
                Color.GREEN
            )

            else -> {
                hueco.setBackgroundColor(Color.RED)
                return false
            }
        }
        //showNextImage()
        return true
    }

    private fun checkAllImagesPlacedCorrectly(): Boolean {
        for (i in 0 until imageAdapter.itemCount) {
            val fragment = supportFragmentManager.findFragmentByTag("f$i")
            val imageView = fragment?.view?.findViewById<ImageView>(R.id.imageView)

            if (imageView != null) {
                val hueco = getHuecoForImage(i)

                // Verificar si la imagen está visible y no está colocada correctamente
                if (imageView.visibility == View.VISIBLE && !checkImagePlacement(
                        imageView.tag?.toString() ?: "", hueco
                    )
                ) {
                    return false
                }

                // Verificar si la imagen no está visible y está colocada correctamente (posiblemente ya arrastrada)
                if (imageView.visibility == View.INVISIBLE && checkImagePlacement(
                        imageView.tag?.toString() ?: "", hueco
                    )
                ) {
                    return false
                }
            }
        }

        // Si llega aquí, significa que todas las imágenes están colocadas correctamente
        return true
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


}
