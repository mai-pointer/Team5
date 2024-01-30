package com.example.didaktikapp.mapFragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.didaktikapp.Crucigrama
import com.example.didaktikapp.DiferenciasActivity
import com.example.didaktikapp.GameManager
import com.example.didaktikapp.MapsActivity
import com.example.didaktikapp.MultipleChoiceActivity
import com.example.didaktikapp.OrdenarImagenesActivity
import com.example.didaktikapp.PuzzleActivity
import com.example.didaktikapp.R
import com.example.didaktikapp.WordSearchActivity

class PlaceDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_place_details, container, false)

        val textViewPlaceName: TextView = rootView.findViewById(R.id.textViewPlaceName)
        val textViewInfo: TextView = rootView.findViewById(R.id.textViewInfo)
        val imageViewPlace: ImageView = rootView.findViewById(R.id.imageViewPlace)
        val btnBack: ImageView = rootView.findViewById(R.id.btnRepetir)

        // Recibe información del lugar desde el fragmento, por ejemplo, usando argumentos
        val args = arguments
        if (args != null) {
            val placeName = args.getString("placeName")
            val placeSnippet = args.getString("placeSnippet")
            val imageUrl = args.getString("imageUrl")

            // Actualiza las vistas con la información del lugar
            textViewPlaceName.text = placeName
            textViewInfo.text = placeSnippet

            // Carga la imagen desde la carpeta drawable
            val imageResId = when (placeName) {
                "Idi probak" -> R.drawable.agricola
                "Txakoli" -> R.drawable.txakoli
                "Udala" -> R.drawable.udala
                "Odolostea" -> R.drawable.harategia
                "Santa Maria" -> R.drawable.santamaria
                "San Mameseko Arkua" -> R.drawable.arkua
                "Lezamako dorrea" -> R.drawable.dorrea
                else -> R.drawable.default_image
            }

            imageViewPlace.setImageResource(imageResId)

            // Configura el botón de retroceso
            btnBack.setOnClickListener {
                // Utiliza remove para asegurarte de que el fragmento se elimine correctamente
                parentFragmentManager.beginTransaction().remove(this).commit()
            }
            // Agrega OnClickListener al TextView del lugar
            textViewPlaceName.setOnClickListener {
                // Lanza la actividad correspondiente al lugar

//****          val intent = getPlaceIntent(placeName)
//****          startActivity(intent)

                GameManager.get()?.startGame(placeName!!)
//                when (placeName) {
//                    "Idi probak" -> GameManager.get()?.startGame("Juego1")
//                    "Odolostea" -> GameManager.get()?.startGame("Juego2")
//                    "Txakoli" -> GameManager.get()?.startGame("Juego3")
//                    "Udala" -> GameManager.get()?.startGame("Juego4")
//                    "Santa Maria" -> GameManager.get()?.startGame("Juego5")
//                    "San Mameseko Arkua" -> GameManager.get()?.startGame("Juego6")
//                    "Lezamako dorrea" -> GameManager.get()?.startGame("Juego7")
//                    else -> Intent(activity, MapsActivity::class.java)
//                }
            }
        }

        return rootView
    }

//    ELIMINAR
    private fun getPlaceIntent(placeName: String?): Intent {
    // Crea un Intent para la actividad correspondiente al lugar
    return when (placeName) {
        "Idi probak" -> Intent(activity, MultipleChoiceActivity::class.java)
        "Odolostea" -> Intent(activity, WordSearchActivity::class.java)
        "Txakoli" -> Intent(activity, DiferenciasActivity::class.java)
        "Udala" -> Intent(activity, DiferenciasActivity::class.java)
        "Santa Maria" -> Intent(activity, PuzzleActivity::class.java)
        "San Mameseko Arkua" -> Intent(activity, Crucigrama::class.java)
        "Lezamako dorrea" -> Intent(activity, OrdenarImagenesActivity::class.java)

        else -> Intent(activity, MapsActivity::class.java)
    }
    }
}
