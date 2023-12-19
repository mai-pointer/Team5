package com.example.didaktikapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.example.didaktikapp.databinding.ActivityMapsBinding
import com.example.didaktikapp.mapFragment.PlaceDetailsFragment
import com.example.didaktikapp.navigation.NavigationUtil
import com.example.didaktikapp.titleFragment.TitleFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CompletableFuture

class MapsActivity : AppCompatActivity(), GoogleMap.OnMapClickListener,
    GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var progressBar: ProgressBar


    // Variable para saber si el usuario es admin o no
    private var esAdmin: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBar = findViewById(R.id.progressBar)

        fragmentHeader(savedInstanceState)

        // Recibe el valor de la variable admin
        esAdmin = intent.getBooleanExtra("admin", false)


        createMap()
    }

    fun createMap() = lifecycleScope.launch {
        progressBar.visibility = View.VISIBLE

        val getMapJob = async(Dispatchers.Main) {
            getMap()
        }
        getMapJob.await()

        progressBar.visibility = View.GONE
    }

    private fun getMap() {
        val mapFragment =  supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync { googleMap ->
            addMarkers(googleMap)
        }
    }

    private fun addMarkers(googleMap: GoogleMap){
        mMap = googleMap
        // Add markers and move the camera
        val idiProbak = LatLng(43.27556360817825, -2.827742396615327)
        val agricolaString = resources.getString(R.string.agricolaText)
        val txakoli = LatLng(43.27758426733325, -2.8308136897866447)
        val txakoliString = resources.getString(R.string.txakoli)
        val udala = LatLng(43.27421110063913, -2.83285560353813)
        val udalaText = resources.getString(R.string.udala)
        val harategia = LatLng(43.27394169280981, -2.832619209726283)
        val harategiaText = resources.getString(R.string.harategia)
        val santaMaria = LatLng(43.27387138926826, -2.8349795537580893)
        val santaMariaText = resources.getString(R.string.santamaria)
        val dorrea = LatLng(43.27279428065491, -2.8434245883650817)
        val dorreaText = resources.getString(R.string.dorrea)
        val arkua = LatLng(43.276383439897, -2.8369511900475195)
        val arkuaText = resources.getString(R.string.arkua)
        val zoomLevel = 14.0f
        mMap.addMarker(
            MarkerOptions().position(idiProbak).title("Idi probak")
                .snippet(agricolaString)
        )
        mMap.addMarker(
            MarkerOptions().position(txakoli).title("Txakoli")
                .snippet(txakoliString)
        )
        mMap.addMarker(
            MarkerOptions().position(udala).title("Udala")
                .snippet(udalaText)
        )
        mMap.addMarker(
            MarkerOptions().position(santaMaria).title("Santa Maria")
                .snippet(santaMariaText)
        )
        mMap.addMarker(
            MarkerOptions().position(harategia).title("Odolostea")
                .snippet(harategiaText)
        )
        mMap.addMarker(
            MarkerOptions().position(dorrea).title("Lezamako dorrea")
                .snippet(dorreaText)
        )
        mMap.addMarker(
            MarkerOptions().position(arkua).title("San Mameseko Arkua")
                .snippet(arkuaText)
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(santaMaria, zoomLevel))

        mMap.setOnMapClickListener(this@MapsActivity)
        mMap.setOnMarkerClickListener(this@MapsActivity)

        progressBar.visibility = View.GONE
    }

    private fun fragmentHeader(savedInstanceState: Bundle?) {
        // Obtén una referencia al contenedor de fragmentos
        val fragmentContainer = findViewById<FrameLayout>(R.id.fragmentContainerView)

        // Reemplaza el contenedor con el TitleFragment
        if (savedInstanceState == null) {
            val titleFragment = TitleFragment.newInstance("Jokoa aukeratu mapan")
            supportFragmentManager.beginTransaction()
                .replace(fragmentContainer.id, titleFragment, "titleFragmentTag")
                .commit()
        }

        // Configura el click listener para el botón en el fragmento
        val titleFragment =
            supportFragmentManager.findFragmentByTag("titleFragmentTag") as TitleFragment?
        titleFragment?.setOnHomeButtonClickListener {
            onHomeButtonClicked()
        }
    }

    override fun onMapClick(point: LatLng) {
        // Este método se ejecuta cuando se hace clic en cualquier parte del mapa
        // Puedes realizar acciones adicionales aquí si es necesario
        Log.d("MapClick", "Click en el mapa: $point")
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        // Se ejecuta cuando se hace clic en un marcador
        // Aquí puedes realizar acciones específicas para el marcador clicado
        Log.d("MarkerClick", "Click en el marcador: ${marker.title}, Snippet: ${marker.snippet}")

        if (isPredefinedMarker(marker.title)) {
            openPlaceDetailsFragment(marker)
        }

        // Devuelve 'true' para indicar que el evento ha sido consumido
        return true
    }

    private fun openPlaceDetailsFragment(marker: Marker) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        val bundle = Bundle()
        bundle.putString("placeName", marker.title)
        bundle.putString("placeSnippet", marker.snippet)

        val fragment = PlaceDetailsFragment()
        fragment.arguments = bundle

        // Cargar la animación desde res/anim
        AnimationUtils.loadAnimation(this, R.anim.slide_up)

        // Asignar la animación al fragmento
        fragmentTransaction.setCustomAnimations(R.anim.slide_up, 0, 0, R.anim.slide_up)

        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun isPredefinedMarker(placeName: String?): Boolean {
        return placeName == "Idi probak" || placeName == "Txakoli" || placeName == "Udala" || placeName == "Odolostea" || placeName == "Santa Maria" || placeName == "Lezamako dorrea" || placeName == "San Mameseko Arkua"
    }

    private fun onHomeButtonClicked() {
        // Acciones a realizar cuando se hace clic en el botón Home
        // Utiliza NavigationUtil para la navegación
        NavigationUtil.navigateToMainMenu(this)
    }
}
