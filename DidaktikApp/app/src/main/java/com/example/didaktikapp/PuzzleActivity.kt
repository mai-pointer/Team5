package com.example.didaktikapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import com.example.didaktikapp.navigation.NavigationUtil
import com.example.didaktikapp.titleFragment.TitleFragment
import kotlin.random.Random

class PuzzleActivity : AppCompatActivity() {

    lateinit var image0: ImageView
    lateinit var image1: ImageView
    lateinit var image2: ImageView
    lateinit var image3: ImageView
    lateinit var image4: ImageView
    lateinit var image5: ImageView
    lateinit var image6: ImageView
    lateinit var image7: ImageView
    lateinit var image8: ImageView

    private val imageArray: Array<Array<Int>> = Array(3) { row ->
        Array(3) { col ->
            row * 3 + col
        }
    }

    private lateinit var changedImageArray: Array<Array<Int>>

    private var myImagesArray =
        arrayOf(
            "image0",
            "image1",
            "image2",
            "image3",
            "image4",
            "image5",
            "image6",
            "image7",
            "image8",
        )
    private var imageViewsMap: MutableMap<Pair<Int, Int>, ImageView> = mutableMapOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puzzle)

        image0 = findViewById(R.id.emptyBlock)
        image1 = findViewById(R.id.block1)
        image2 = findViewById(R.id.block2)
        image3 = findViewById(R.id.block3)
        image4 = findViewById(R.id.block4)
        image5 = findViewById(R.id.block5)
        image6 = findViewById(R.id.block6)
        image7 = findViewById(R.id.block7)
        image8 = findViewById(R.id.block8)

        imageViewsMap = mutableMapOf(Pair(0, 0) to image0,
        Pair(0, 1) to image1,
        Pair(0, 2) to image2,
        Pair(1, 0) to image3,
        Pair(1, 1) to image4,
        Pair(1, 2) to image5,
        Pair(2, 0) to image6,
        Pair(2, 1) to image7,
        Pair(2, 2) to image8)

        image0.setOnClickListener {
            val pair = findPairByImageView(image0)
            if (pair != null) {
                onImageClick(pair)
            }
        }
        image1.setOnClickListener {
            val pair = findPairByImageView(image1)
            if (pair != null) {
                onImageClick(pair)
            }
        }
        image2.setOnClickListener {
            val pair = findPairByImageView(image2)
            if (pair != null) {
                onImageClick(pair)
            }
        }
        image3.setOnClickListener {
            val pair = findPairByImageView(image3)
            if (pair != null) {
                onImageClick(pair)
            }
        }
        image4.setOnClickListener {
            val pair = findPairByImageView(image4)
            if (pair != null) {
                onImageClick(pair)
            }
        }
        image5.setOnClickListener {
            val pair = findPairByImageView(image5)
            if (pair != null) {
                onImageClick(pair)
            }
        }
        image6.setOnClickListener {
            val pair = findPairByImageView(image6)
            if (pair != null) {
                onImageClick(pair)
            }
        }
        image7.setOnClickListener {
            val pair = findPairByImageView(image7)
            if (pair != null) {
                onImageClick(pair)
            }
        }
        image8.setOnClickListener {
            val pair = findPairByImageView(image8)
            if (pair != null) {
                onImageClick(pair)
            }
        }

        // Obtén una referencia al contenedor de fragmentos
        val fragmentContainer = findViewById<FrameLayout>(R.id.fragmentContainerView)

        // Reemplaza el contenedor con el TitleFragment
        if (savedInstanceState == null) {
            val titleFragment = TitleFragment.newInstance("Puzzle")
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

        val flattenedList = imageArray.flatten().toMutableList()
        flattenedList.shuffle()

        changedImageArray = Array(3) { row ->
            Array(3) { col ->
                flattenedList[row * 3 + col]
            }
        }
        pintarImagenes()

    }
    fun findPairByImageView(imageView: ImageView): Pair<Int, Int>? {
        return imageViewsMap.entries.find { it.value == imageView }?.key
    }

    fun findImageViewByPair(pair: Pair<Int, Int>): ImageView? {
        return imageViewsMap[pair]
    }

    private fun onHomeButtonClicked() {
        // Acciones a realizar cuando se hace clic en el botón Home
        // Utiliza NavigationUtil para la navegación
        NavigationUtil.navigateToMainMenu(this)
    }

    private fun pintarImagenes() {
        image0.setImageResource(
            resources.getIdentifier(
                myImagesArray[changedImageArray[0][0]],
                "drawable",
                packageName
            )
        )
        image1.setImageResource(
            resources.getIdentifier(
                myImagesArray[changedImageArray[0][1]],
                "drawable",
                packageName
            )
        )
        image2.setImageResource(
            resources.getIdentifier(
                myImagesArray[changedImageArray[0][2]],
                "drawable",
                packageName
            )
        )
        image3.setImageResource(
            resources.getIdentifier(
                myImagesArray[changedImageArray[1][0]],
                "drawable",
                packageName
            )
        )
        image4.setImageResource(
            resources.getIdentifier(
                myImagesArray[changedImageArray[1][1]],
                "drawable",
                packageName
            )
        )
        image5.setImageResource(
            resources.getIdentifier(
                myImagesArray[changedImageArray[1][2]],
                "drawable",
                packageName
            )
        )
        image6.setImageResource(
            resources.getIdentifier(
                myImagesArray[changedImageArray[2][0]],
                "drawable",
                packageName
            )
        )
        image7.setImageResource(
            resources.getIdentifier(
                myImagesArray[changedImageArray[2][1]],
                "drawable",
                packageName
            )
        )
        image8.setImageResource(
            resources.getIdentifier(
                myImagesArray[changedImageArray[2][2]],
                "drawable",
                packageName
            )
        )
    }

    private fun onImageClick(coordinates:Pair<Int, Int>) {
        var myDirection = 0
        var attemps = 0
        val maxAttemps = 3
        while (!canMove(coordinates, Direction.values()[myDirection]) && attemps <= maxAttemps) {
            myDirection += 1
            Log.d("Puzle", attemps.toString())
            attemps += 1
            Log.d("Puzle", attemps.toString())
        }
        if (attemps <= maxAttemps) {
            changeImage(coordinates, Direction.values()[myDirection])
        } else {
            Log.d("Puzle", "No se puede mover esta ficha")
        }
    }

    enum class Direction {
        UP, DOWN, LEFT, RIGTH
    }

    fun canMove(coordinates:Pair<Int,Int>, direction: Direction): Boolean {
        val row = coordinates.first
        val col = coordinates.second
        when (direction) {
            Direction.UP -> if (row - 1 < 0 || changedImageArray[row - 1][col] > 0) return false
            Direction.DOWN -> if (row + 1 > 2 || changedImageArray[row + 1][col] > 0) return false
            Direction.LEFT -> if (col - 1 < 0 || changedImageArray[row][col - 1] > 0) return false
            Direction.RIGTH -> if (col + 1 > 2 || changedImageArray[row][col + 1] > 0) return false
        }
        return true
    }

    fun changeImage(coordinates:Pair<Int,Int>, direction: Direction) {
        val row = coordinates.first
        val col = coordinates.second
        when (direction) {
            Direction.UP -> {
                val newCoordinates:Pair<Int, Int> = Pair(row - 1, col)
                val myCurrentImage = myImagesArray[changedImageArray[row][col]]
                val targetImage = findImageViewByPair(newCoordinates)
                targetImage?.setImageResource(resources.getIdentifier(myCurrentImage, "drawable", packageName))

                val emptyImage = findImageViewByPair(coordinates)
                emptyImage?.setImageResource(resources.getIdentifier("image0", "drawable", packageName))

                val myVal = changedImageArray[row][col]
                changedImageArray[row][col] = 0
                changedImageArray[row-1][col] = myVal
            }
            Direction.DOWN -> {
                val newCoordinates:Pair<Int, Int> = Pair(row + 1, col)
                val myCurrentImage = myImagesArray[changedImageArray[row][col]]
                val targetImage = findImageViewByPair(newCoordinates)
                targetImage?.setImageResource(resources.getIdentifier(myCurrentImage, "drawable", packageName))

                val emptyImage = findImageViewByPair(coordinates)
                emptyImage?.setImageResource(resources.getIdentifier("image0", "drawable", packageName))

                val myVal = changedImageArray[row][col]
                changedImageArray[row][col] = 0
                changedImageArray[row+1][col] = myVal

            }
            Direction.LEFT -> {
                val newCoordinates:Pair<Int, Int> = Pair(row, col-1)
                val myCurrentImage = myImagesArray[changedImageArray[row][col]]
                val targetImage = findImageViewByPair(newCoordinates)
                targetImage?.setImageResource(resources.getIdentifier(myCurrentImage, "drawable", packageName))

                val emptyImage = findImageViewByPair(coordinates)
                emptyImage?.setImageResource(resources.getIdentifier("image0", "drawable", packageName))

                val myVal = changedImageArray[row][col]
                changedImageArray[row][col] = 0
                changedImageArray[row][col-1] = myVal
            }
            Direction.RIGTH -> {
                val newCoordinates:Pair<Int, Int> = Pair(row, col+1)
                val myCurrentImage = myImagesArray[changedImageArray[row][col]]
                val targetImage = findImageViewByPair(newCoordinates)
                targetImage?.setImageResource(resources.getIdentifier(myCurrentImage, "drawable", packageName))

                val emptyImage = findImageViewByPair(coordinates)
                emptyImage?.setImageResource(resources.getIdentifier("image0", "drawable", packageName))

                val myVal = changedImageArray[row][col]
                changedImageArray[row][col] = 0
                changedImageArray[row][col+1] = myVal
            }
        }
        if (isPuzzleComplete()) {
            showPuzzleCompleteToast()
        }
    }

    private fun isPuzzleComplete(): Boolean {
        val flatChangedArray = changedImageArray.flatten()
        return flatChangedArray.withIndex().all { (index, value) -> index == value }
    }
    private fun showPuzzleCompleteToast() {
        // Muestra un Toast indicando que el rompecabezas está completo
        Toast.makeText(this, "Puzlea bukatuta. Zorionak!", Toast.LENGTH_SHORT).show()
    }

}
