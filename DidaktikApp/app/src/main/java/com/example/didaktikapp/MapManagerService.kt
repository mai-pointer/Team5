package com.example.didaktikapp

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*

class MapManagerService : Service() {
    private var gameManagerService: GameManagerService? = GameManagerService()
    var isPlaying = false

    private val miJob = Job()

    // Almacena la información de las ubicaciones del mapa
    private val mapLocations = mutableMapOf<String, LatLng>()

    // Variables
    private lateinit var context: Context
    var currentLocationIndex = 0
    private var currentGameFromDB = ""
    private var myCurrentPosition: Location? = null


    // Binder para la conexión con la actividad
    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): MapManagerService = this@MapManagerService
    }

    override fun onBind(intent: Intent?): IBinder? {

        return binder
    }

    fun initialize(context: Context, esAdmin: Boolean) {
        this.context = context
        // BD - Iniciar ---
        BDManager.Iniciar{ partidaDao, competitivoDao, sharedPreferences ->
            GlobalScope.launch(Dispatchers.IO){
                val partida = partidaDao.get(sharedPreferences.getInt("partida_id", -1))
                currentLocationIndex = partida.juegoMapa
                currentGameFromDB = partida.juego

                Log.i("PARTIDA", "Cargado: " + currentLocationIndex)

            }
        }

        initializeMapLocations()
    }

    @SuppressLint("MissingPermission")

    private fun initializeMapLocations() {
//        mapLocations.put("Idi probak", LatLng(43.257557, -2.902372))
        mapLocations.put("Idi probak", LatLng(43.27556360817825, -2.827742396615327))
        mapLocations.put("Odolostea", LatLng(43.27394169280981, -2.832619209726283))
        mapLocations.put("Txakoli", LatLng(43.27758426733325, -2.8308136897866447))
        mapLocations.put("Udala", LatLng(43.27421110063913, -2.83285560353813))
        mapLocations.put("Santa Maria", LatLng(43.27387138926826, -2.8349795537580893))
        mapLocations.put("San Mameseko Arkua", LatLng(43.276383439897, -2.8369511900475195))
        mapLocations.put("Lezamako dorrea", LatLng(43.27279428065491, -2.8434245883650817))
    }

    fun getLocationsToShow(esAdmin:Boolean): MutableMap<String, LatLng> {
        val myString = mapLocations.keys.elementAt(currentLocationIndex)
        val coordinates = getCurrentLocation()
        val myLocation:MutableMap<String, LatLng> = mutableMapOf (myString to coordinates)
        return if (esAdmin) {
            mapLocations
        } else {
            myLocation
        }
    }

    fun checkProximity(currentUserPos:Location?){
        val targetLocation = getCurrentLocation()
        val distance = calculateDistance(currentUserPos!!.latitude, currentUserPos!!.longitude, targetLocation.latitude, targetLocation.longitude)
        val proximityThreshold = 50
        if (distance <= proximityThreshold){
            gameManagerService = GameManager.get()
            gameManagerService!!.startGame(mapLocations.keys.elementAt(currentLocationIndex))
            isPlaying = true
        }
    }

    fun stopMaps() {
        val intent = Intent(this, MapsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.putExtra("EXIT", true)
        startActivity(intent)
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }

    // Obtiene la ubicación actual del mapa
    fun getCurrentLocation(): LatLng {
        return mapLocations.values.elementAt(currentLocationIndex)
    }


    // Muestra la siguiente ubicación del mapa
    fun showNextLocation() {
        if (currentLocationIndex < mapLocations.size - 1) {
            currentLocationIndex++
            notifyLocationChanged()

            val index = currentLocationIndex

            // BD - Guardar ----
            BDManager.Iniciar{ partidaDao, competitivoDao, sharedPreferences ->
                GlobalScope.launch(Dispatchers.IO){
                    var partida = partidaDao.get(sharedPreferences.getInt("partida_id", -1))
                    var index = currentLocationIndex

                    Log.i("PARTIDA", "Index: " + index)


                    var partidaNueva = partida

                    partidaNueva.juegoMapa = index

                    partidaDao.update(
                        partidaNueva
                    )

                    Log.i("PARTIDA", "Update: " + partidaNueva.juegoMapa)

                    delay(1000)

                    val intent = Intent(context, MapsActivity::class.java)
                    context.startActivity(intent)
                }
            }

        } else {
//            -----------------------------------------------------------------------------------
//            MapManager.destroy()
//            gameManagerService = GameManager.get()
//            gameManagerService?.startGame("AMAIERAKO JARDUERA")
//            -----------------------------------------------------------------------------------
        }
    }

    // Notifica a través de un Intent que la ubicación ha cambiado
    private fun notifyLocationChanged() {
        val intent = Intent("com.example.didaktikapp.LOCATION_CHANGED")
        intent.putExtra("location", getCurrentLocation())
        context.sendBroadcast(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        miJob.cancel()
    }

    //Singleton
    object MapManager {
        private var mapManagerService: MapManagerService? = null

        //Inicializa el singleton
        fun initialize(context: Context, esAdmin: Boolean) {
            if (mapManagerService == null) {
                mapManagerService = MapManagerService()
                mapManagerService?.initialize(context, esAdmin)
            }
        }

        //Devuelve el servicio
        fun get(): MapManagerService? {
            return mapManagerService
        }

        fun destroy() {
            mapManagerService = null
        }
    }
}
