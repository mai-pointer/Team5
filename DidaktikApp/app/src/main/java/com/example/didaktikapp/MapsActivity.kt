package com.example.didaktikapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.didaktikapp.MapManagerService.MapManager
import com.example.didaktikapp.databinding.ActivityMapsBinding
import com.example.didaktikapp.mapFragment.PlaceDetailsFragment
import com.example.didaktikapp.navigation.NavigationUtil
import com.example.didaktikapp.titleFragment.TitleFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener,
    GoogleMap.OnMarkerClickListener {

    private var hasShownFirstLocation: Boolean = false
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var progressBar: ProgressBar
    private var mapManagerService: MapManagerService? = MapManagerService()
    private var updateJob: Job? = null
    private var marker: Marker? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var myCurrentPosition: Location? = null

    // Variable para saber si el usuario es admin o no
    private var esAdmin: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mapManagerService = MapManager.get()
        if(mapManagerService == null){
            MapManager.initialize(this, esAdmin)
            mapManagerService = MapManager.get()
        }
        mapManagerService!!.isPlaying = false

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        esAdmin = intent.getBooleanExtra("admin", false)
        progressBar = findViewById(R.id.progressBar)

        setupHeaderFragment(savedInstanceState)

        if(!esAdmin){
            checkPermissions()
        }else{
            initMap()
        }
        progressBar.visibility = View.VISIBLE

    }

    private fun initMap(){
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupHeaderFragment(savedInstanceState: Bundle?) {
        val fragmentContainer = findViewById<FrameLayout>(R.id.titleFragmentTag)
        if (savedInstanceState == null) {
            val titleFragment = TitleFragment.newInstance(resources.getString(R.string.mapTitle))
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

        if (hasShownFirstLocation){
            mapManagerService?.showNextLocation()
        }

        hasShownFirstLocation = true

        val locationsToShow = mapManagerService?.getLocationsToShow(esAdmin)
        var referenceMarker: LatLng? = null
        if (locationsToShow != null) {
            for (location in locationsToShow) {
                addMarker(location.value, location.key)
                referenceMarker = location.value
            }
        }

        val zoomLevel = 14.0f

        if (esAdmin){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(referenceMarker!!, zoomLevel))
            mMap.setOnMarkerClickListener(this)
        }else{
            val myPos = LatLng (myCurrentPosition!!.latitude, myCurrentPosition!!.longitude)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos, zoomLevel))
        }

        mMap.setOnMapClickListener(this)
        mMap.setOnMapLoadedCallback {
            progressBar.visibility = View.GONE

            if(!esAdmin){
                updateJob = CoroutineScope(Dispatchers.Main).launch {
                    updateMarkerPosition()
                }
            }
        }
    }

    suspend fun updateMarkerPosition() {
        while (true) {
            val newPosition = LatLng(myCurrentPosition!!.latitude, myCurrentPosition!!.longitude)
            withContext(Dispatchers.Main) {
                if (marker == null) {
                    marker = mMap.addMarker(MarkerOptions().position(newPosition).icon(
                        BitmapDescriptorFactory.fromResource(R.drawable.posicon)))
                } else {
                    marker?.position = newPosition
                }
            }
            delay(1000)
            //Toast.makeText(this, "Latitude: ${newPosition.latitude}, Longitude: ${newPosition.longitude} ", Toast.LENGTH_SHORT).show()
        }
    }



    // Añade un marcador en la ubicación proporcionada
    private fun addMarker(location: LatLng, titulo:String) {
        val snippet = getSnippetForLocation(location)
        mMap.addMarker(MarkerOptions().position(location).title(titulo).snippet(snippet))
    }

    // Obtiene el snippet correspondiente a la ubicación dada
    private fun getSnippetForLocation(location: LatLng): String {
        return "Snippet para ${location.latitude}, ${location.longitude}"
    }

    override fun onMapClick(point: LatLng) {
        Log.d("MapClick", "Click en el mapa: $point")
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        Log.d("MarkerClick", "Click en el marcador: ${marker.title}, Snippet: ${marker.snippet}")
        openPlaceDetailsFragment(marker)

        return true
    }

    private fun openPlaceDetailsFragment(marker: Marker) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        val text:String = when (marker.title) {
            "Idi probak" -> this.getString(R.string.agricolaText)
            "Txakoli" -> this.getString(R.string.txakoli)
            "Udala" -> this.getString(R.string.udala)
            "Odolostea" -> this.getString(R.string.harategia)
            "Santa Maria" -> this.getString(R.string.santamaria)
            "San Mameseko Arkua" -> this.getString(R.string.arkua)
            "Lezamako dorrea" -> this.getString(R.string.dorrea)
            else -> "LoremIpsum"
        }

        val bundle = Bundle()
        bundle.putString("placeName", marker.title)
        bundle.putString("placeSnippet", text)

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

    private fun onHomeButtonClicked() { NavigationUtil.navigateToMainMenu(this) }

    private fun checkPermissions(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestLocationPermision()
        }else{
            getUserLocation()
        }
    }

    fun requestLocationPermision(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(this, "Lokalizatze baimenak baztertu dituzu. Mesedez onartu lokalizatze baimenak.", Toast.LENGTH_LONG).show()
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 777)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 777){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                getUserLocation()
            }else{
                Toast.makeText(this, "Baimenak baztertu dituzu", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun getUserLocation(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    myCurrentPosition = it
                    initMap()
                } else {
                    Log.d("Ubicacion", "No se pudo obtener la ubicación")
                }
            }
            val locationRequest = LocationRequest.create().apply {
                interval = 5000
                fastestInterval = 2000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult
                    Log.d("Ubicacion", "Se recibió una actualización")
                    for (location in locationResult.locations) {
                        myCurrentPosition = location
                    }
                    if(!mapManagerService!!.isPlaying){
                        mapManagerService!!.checkProximity(myCurrentPosition)
                    }

                }
            }
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )

        }catch (e: SecurityException) {
            Log.d("Ubicacion", "Tal vez no solicitaste permiso antes")
        }
    }

    fun returnPlayerPos(): Location?{
        return myCurrentPosition
    }

    override fun onDestroy() {
        super.onDestroy()
        updateJob?.cancel()
    }
}
