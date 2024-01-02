package com.example.didaktikapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.example.didaktikapp.databinding.ActivityMapsBinding
import com.example.didaktikapp.mapFragment.PlaceDetailsFragment
import com.example.didaktikapp.navigation.NavigationUtil
import com.example.didaktikapp.titleFragment.TitleFragment
import com.example.didaktikapp.MapManagerService.MapManager

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener,
    GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var progressBar: ProgressBar
    private lateinit var mapManagerService: MapManagerService

    // Variable para saber si el usuario es admin o no
    var esAdmin: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBar = findViewById(R.id.progressBar)
        MapManager.initialize(this)
        mapManagerService = MapManager.get()!!

        setupHeaderFragment(savedInstanceState)

        // Recibe el valor de la variable admin
        esAdmin = intent.getBooleanExtra("admin", false)

        val mapManagerIntent = Intent(this, MapManagerService::class.java)
        startService(mapManagerIntent)

        mapManagerService = MapManagerService()

        progressBar.visibility = View.VISIBLE
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    private fun setupHeaderFragment(savedInstanceState: Bundle?) {
        val fragmentContainer = findViewById<FrameLayout>(R.id.fragmentContainerView)
        if (savedInstanceState == null) {
            val titleFragment = TitleFragment.newInstance("Jokoa aukeratu mapan")
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

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val locationsToShow = mapManagerService.getLocationsToShow(esAdmin)

        for (location in locationsToShow) {
            addMarker(location)
        }

        val zoomLevel = 14.0f
        val myPos = LatLng (mapManagerService.myLocation().latitude, mapManagerService.myLocation().longitude)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos, zoomLevel))

        mMap.setOnMapClickListener(this)

        if (esAdmin){
            mMap.setOnMarkerClickListener(this)
        }

        mMap.setOnMapLoadedCallback {
            progressBar.visibility = View.GONE
        }
    }



    // Añade un marcador en la ubicación proporcionada
    private fun addMarker(location: LatLng) {
        mMap.clear() // Borra todos los marcadores existentes
        val snippet = getSnippetForLocation(location) // Implementa esta función según tus necesidades
        mMap.addMarker(MarkerOptions().position(location).title("Ubicación Actual").snippet(snippet))
    }

    // Obtiene el snippet correspondiente a la ubicación dada
    private fun getSnippetForLocation(location: LatLng): String {
        return "Snippet para ${location.latitude}, ${location.longitude}"
    }

    override fun onMapClick(point: LatLng) {
        // Este método se ejecuta cuando se hace clic en cualquier parte del mapa
        // Puedes realizar acciones adicionales aquí si es necesario
        Log.d("MapClick", "Click en el mapa: $point")
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        Log.d("MarkerClick", "Click en el marcador: ${marker.title}, Snippet: ${marker.snippet}")

        if (isPredefinedMarker(marker.title)) {
            openPlaceDetailsFragment(marker)
        }

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
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)

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
