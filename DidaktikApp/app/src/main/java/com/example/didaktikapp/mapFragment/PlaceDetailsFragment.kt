package com.example.didaktikapp.mapFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.didaktikapp.GameManager
import com.example.didaktikapp.R

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
                GameManager.get()?.startGame(placeName!!)
            }

            rootView.findViewById<Button>(R.id.jugarBtnFragmento).setOnClickListener {
                // Lanza la actividad correspondiente al lugar
                GameManager.get()?.startGame(placeName!!)
            }
        }

        return rootView
    }

}
