package com.example.didaktikapp.titleFragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.example.didaktikapp.MainMenuActivity
import com.example.didaktikapp.R

class TitleFragment : Fragment() {

    // Parámetros para el fragmento
    private var title: String? = null
    private var subTitle: String? = null
    private var homeButtonListener: View.OnClickListener? = null
    fun setOnHomeButtonClickListener(listener: View.OnClickListener) {
        homeButtonListener = listener
    }

    fun onHomeButtonClicked() {
        // Acciones a realizar cuando se hace clic en el botón Home
        // Por ejemplo, transición a MainMenuActivity
        val intent = Intent(requireContext(), MainMenuActivity::class.java)
        startActivity(intent)
        requireActivity().finish()  // Cierra la actividad actual
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Obtiene los argumentos proporcionados al fragmento
        arguments?.let {
            title = it.getString(ARG_TITLE)
            subTitle = it.getString(ARG_SUBTITLE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla el diseño para este fragmento
        val view = inflater.inflate(R.layout.fragment_title, container, false)

        // Configura el título y el subtítulo si están disponibles
        view.findViewById<TextView>(R.id.textViewTitle)?.text = title

        return view
    }

    // Método para configurar el título del fragmento
    fun setTitle(title: String) {
        this.title = title
        // Actualiza la vista si ya está creada
        view?.findViewById<TextView>(R.id.textViewTitle)?.text = title
    }

    companion object {
        // Constantes para los argumentos
        private const val ARG_TITLE = "title"
        private const val ARG_SUBTITLE = "subtitle"

        @JvmStatic
        fun newInstance(title: String, subtitle: String?) =
            TitleFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putString(ARG_SUBTITLE, subtitle)
                }
            }
    }
}
