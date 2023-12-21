package com.example.didaktikapp

// MapManagerService.kt

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.google.android.gms.maps.model.LatLng

class MapManagerService : Service() {

    // Almacena la información de las ubicaciones del mapa
    val mapLocations = mutableMapOf<String, LatLng>()

    // Variables
    private lateinit var context: Context
    private var currentLocationIndex = 0

    // Inicializa el servicio
    fun initialize(context: Context) {
        this.context = context
        // Agrega las ubicaciones del mapa según tu lógica
        initializeMapLocations()
    }

    // Agrega las ubicaciones iniciales del mapa
    private fun initializeMapLocations() {
        mapLocations.put("Idi Probak", LatLng(43.27556360817825, -2.827742396615327))
        mapLocations.put("Harategia", LatLng(43.27394169280981, -2.832619209726283))
        mapLocations.put("Txakoli", LatLng(43.27758426733325, -2.8308136897866447))
        mapLocations.put("Udala", LatLng(43.27421110063913, -2.83285560353813))
        mapLocations.put("Santa Maria", LatLng(43.27387138926826, -2.8349795537580893))
        mapLocations.put("Arkua", LatLng(43.276383439897, -2.8369511900475195))
        mapLocations.put("Dorrea", LatLng(43.27279428065491, -2.8434245883650817))
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    // Obtiene la ubicación actual del mapa
    fun getCurrentLocation(): LatLng? {
        return if (currentLocationIndex < mapLocations.size) {
            mapLocations.values.elementAt(currentLocationIndex)
        } else {
            null
        }
    }

    // Muestra la siguiente ubicación del mapa
    fun showNextLocation() {
        if (currentLocationIndex < mapLocations.size - 1) {
            // Si hay más ubicaciones, muestra la siguiente
            currentLocationIndex++
            notifyLocationChanged()
        } else {
            // Si no, realiza alguna acción, como volver al menú principal
            // Puedes agregar tu lógica aquí
            val intent = Intent(context, MapsActivity::class.java)
            context.startActivity(intent)
        }
    }

    // Notifica a través de un Intent que la ubicación ha cambiado
    private fun notifyLocationChanged() {
        val intent = Intent("com.example.didaktikapp.LOCATION_CHANGED")
        intent.putExtra("location", getCurrentLocation())
        context.sendBroadcast(intent)
    }
}

/*private var mapManagerService: MapManagerService? = null

// ...

// Inicializa el servicio en el método onCreate
mapManagerService = MapManagerService()
mapManagerService?.initialize(this)

// ...

// En el método onMarkerClick
private fun onMarkerClick(marker: Marker): Boolean {
    // ...

    if (isPredefinedMarker(marker.title)) {
        openPlaceDetailsFragment(marker)
        mapManagerService?.showNextLocation()
    }

    // ...
}*/