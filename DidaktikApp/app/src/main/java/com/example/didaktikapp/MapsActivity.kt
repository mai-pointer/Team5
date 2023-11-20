package com.example.didaktikapp

import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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
        val lezamaUdala = LatLng(43.27424563392292, -2.832868930631767)
        val santaMaria = LatLng(43.27386287112582, -2.8349717824168814)
        val frontoia = LatLng(43.273644148421766, -2.833196160210576)
        val zoomLevel = 15.0f
        mMap.addMarker(
            MarkerOptions().position(lezamaUdala).title("Lezamako Udala")
                .snippet("Texto personalizado para Lezamako Udala")
        )
        mMap.addMarker(
            MarkerOptions().position(santaMaria).title("Santa Maria")
                .snippet("Texto personalizado para Santa Maria")
        )
        mMap.addMarker(
            MarkerOptions().position(frontoia).title("Frontoia")
                .snippet("Texto personalizado para Frontoia")
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lezamaUdala, zoomLevel))

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

        // Asigna una URL de imagen específica para cada marcador
        val imageUrl: String = when (marker.title) {
            "Lezamako Udala" -> "https://upload.wikimedia.org/wikipedia/commons/b/b3/Lezama_-_Ayuntamiento.jpg"
            "Santa Maria" -> "https://lh5.googleusercontent.com/p/AF1QipP1Yfn3TJMz8nDxUEs4KpmttBt_auqHEiZJlKH_=w426-h240-k-no"
            "Frontoia" -> "https://lh5.googleusercontent.com/p/AF1QipNkRuDv1lV45xQHb4itWcv99mNX-s71rMJZaukw=w408-h306-k-no"
            else -> "https://ejemplo.com/imagen_predeterminada.jpg"
            // Puedes usar una imagen predeterminada si no hay coincidencia
        }

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
        return placeName == "Lezamako Udala" || placeName == "Santa Maria" || placeName == "Frontoia"
    }
}
