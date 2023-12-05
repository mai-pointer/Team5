package com.example.didaktikapp.titleFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import com.example.didaktikapp.R
import com.example.didaktikapp.navigation.NavigationUtil

class TitleFragment : Fragment() {

    private var homeButtonClickListener: View.OnClickListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_title, container, false)

        // Configure title
        val titleText = arguments?.getString(ARG_TITLE) ?: "Fragment Title"
        view.findViewById<TextView>(R.id.textViewTitle).text = titleText

        // Set click listener for the home button
        view.findViewById<ImageButton>(R.id.btnHome).setOnClickListener {
            onHomeButtonClicked(it)
        }

        return view
    }

    private fun onHomeButtonClicked(view: View) {
        // Actions to perform when the home button is clicked
        homeButtonClickListener?.onClick(view) ?: run {
            // Si no se ha configurado un listener, realizar la navegación predeterminada
            val activity = requireActivity() as? AppCompatActivity
            activity?.let {
                NavigationUtil.navigateToMainMenu(it)
            }
        }
    }

    fun setOnHomeButtonClickListener(listener: View.OnClickListener) {
        homeButtonClickListener = listener
    }

    companion object {
        private const val ARG_TITLE = "title"

        @JvmStatic
        fun newInstance(title: String) =
            TitleFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                }
            }
    }
}
