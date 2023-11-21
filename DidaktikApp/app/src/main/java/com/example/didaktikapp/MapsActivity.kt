package com.example.didaktikapp

import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
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

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener,
    GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add markers and move the camera
        val coopAgricola = LatLng(43.27556360817825, -2.827742396615327)
        val agricolaString = resources.getString(R.string.agricolaText)
        val txakoli = LatLng(43.27758426733325, -2.8308136897866447)
        val txakoliString = resources.getString(R.string.txakoli)
        val udala = LatLng(43.27421110063913, -2.83285560353813)
        val udalaText = resources.getString(R.string.udala)
        val zoomLevel = 15.0f
        mMap.addMarker(
            MarkerOptions().position(coopAgricola).title("Coop. Agrícola")
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coopAgricola, zoomLevel))

        mMap.setOnMapClickListener(this)
        mMap.setOnMarkerClickListener(this)
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
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)

        // Asignar la animación al fragmento
        fragmentTransaction.setCustomAnimations(R.anim.slide_up, 0, 0, R.anim.slide_up)

        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun isPredefinedMarker(placeName: String?): Boolean {
        return placeName == "Coop. Agrícola" || placeName == "Txakoli" || placeName == "Udala"
    }
}
