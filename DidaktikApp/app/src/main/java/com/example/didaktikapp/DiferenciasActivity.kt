package com.example.didaktikapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.didaktikapp.navigation.NavigationUtil
import com.example.didaktikapp.titleFragment.TitleFragment

class DiferenciasActivity : AppCompatActivity() {
    private val repeatActivityMenu = RepeatActivityMenu(this)
    lateinit var botones: List<Button>
    lateinit var diferencias: List<ConstraintLayout>

    var contDifs = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diferencias)

        // Obtén una referencia al contenedor de fragmentos
        val fragmentContainer = findViewById<FrameLayout>(R.id.fragmentContainerView)

        // Reemplaza el contenedor con el TitleFragment
        if (savedInstanceState == null) {
            val titleFragment = TitleFragment.newInstance("Ezberdintasunak")
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


        botones = listOf(
            findViewById(R.id.btnDife6),
            findViewById(R.id.btnDife5),
            findViewById(R.id.btnDife4),
            findViewById(R.id.btnDife3),
            findViewById(R.id.btnDife2),
            findViewById(R.id.btnDife1)
        )

        diferencias = listOf(
            findViewById(R.id.dife6),
            findViewById(R.id.dife5),
            findViewById(R.id.dife4),
            findViewById(R.id.dife3),
            findViewById(R.id.dife2),
            findViewById(R.id.dife1)
        )


        ocultarDiferencias()

        for (i in botones.indices) {
            botones[i].setOnClickListener {
                diferencias[i].background.alpha = 255
                botones[i].isEnabled = false
                actualizarContador()
            }
        }
    }

    private fun ocultarDiferencias() {
        for (diferencia in diferencias) {
            diferencia.background.alpha = 0
        }
    }

    private fun actualizarContador() {
        var btnCont = findViewById<Button>(R.id.btnJugar)
        var imgTouch = findViewById<ImageView>(R.id.clickimg)

        if (contDifs ==5) {
            //Ganaste!
            val intent = Intent(this ,DiferenciasActivity::class.java)
            repeatActivityMenu.showGameOverDialog(this, intent)

        }
        else {
            contDifs++
            btnCont.text = contDifs.toString()
            if (contDifs == 1){
                imgTouch.visibility = ImageView.INVISIBLE
            }
        }
    }
    private fun onHomeButtonClicked() {
        // Acciones a realizar cuando se hace clic en el botón Home
        // Utiliza NavigationUtil para la navegación
        NavigationUtil.navigateToMainMenu(this)
    }
}