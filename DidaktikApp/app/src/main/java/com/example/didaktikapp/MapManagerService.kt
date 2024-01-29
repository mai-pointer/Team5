package com.example.didaktikapp

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*

class MapManagerService : Service() {
    private var gameManagerService: GameManagerService? = GameManagerService()

    private val miJob = Job()
    private val miScope = CoroutineScope(Dispatchers.Default + miJob)

    // Almacena la información de las ubicaciones del mapa
    private val mapLocations = mutableMapOf<String, LatLng>()

    // Variables
    private lateinit var context: Context
    private var currentLocationIndex = 0
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
        // BD ---
        BDManager.Iniciar{ partidaDao, competitivoDao, sharedPreferences ->
            GlobalScope.launch(Dispatchers.IO){
                val partida = partidaDao.get(sharedPreferences.getInt("partida_id", -1))
                currentLocationIndex = partida.juegoMapa ?: 0
                currentGameFromDB = partida.juego

            }
        }



        initializeMapLocations()

        if (!esAdmin){
            updateLocation()
        }

    }

    @SuppressLint("MissingPermission")
    fun updateLocation(){
        val locationManager : LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        runBlocking {
            val myPos: Deferred<Location?> = async {
                return@async locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            }
            myCurrentPosition = myPos.await()
        }

        miScope.launch {
            while (miScope.isActive) {
                myCurrentPosition = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                Log.d("MyCurrentPosition", myCurrentPosition.toString())
                var currentLatLng = LatLng(myCurrentPosition!!.latitude, myCurrentPosition!!.longitude)
                if (checkProximity(currentLatLng)){
                    gameManagerService = GameManager.get()
                    //val myGame = gameManagerService!!.juegoActual()
                    gameManagerService!!.startGame(currentGameFromDB)
                    showNextLocation()
                    stopSelf()
                }
                delay(5000)
            }
        }
    }
    private fun initializeMapLocations() {
//        mapLocations.put("Idi probak", LatLng(43.27556360817825, -2.827742396615327))
//        mapLocations.put("Odolostea", LatLng(43.27394169280981, -2.832619209726283))
//        mapLocations.put("Txakoli", LatLng(43.27758426733325, -2.8308136897866447))
//        mapLocations.put("Udala", LatLng(43.27421110063913, -2.83285560353813))
//        mapLocations.put("Santa Maria", LatLng(43.27387138926826, -2.8349795537580893))
//        mapLocations.put("San Mameseko Arkua", LatLng(43.276383439897, -2.8369511900475195))
//        mapLocations.put("Lezamako dorrea", LatLng(43.27279428065491, -2.8434245883650817))
        mapLocations.put("Idi probak", LatLng(43.257559, -2.902346))
        mapLocations.put("Odolostea", LatLng(43.257011, -2.903898))
    }

    fun myPosition(): Location? {
        return myCurrentPosition
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

    private fun checkProximity(currentUserPos:LatLng):Boolean{
        val targetLocation = getCurrentLocation()
        val distance = calculateDistance(currentUserPos!!.latitude, currentUserPos!!.longitude, targetLocation.latitude, targetLocation.longitude)
        val proximityThreshold = 50
        return distance <= proximityThreshold
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

            // BD ----
            BDManager.Iniciar{ partidaDao, competitivoDao, sharedPreferences ->
                GlobalScope.launch(Dispatchers.IO){
                    val partida = partidaDao.get(sharedPreferences.getInt("partida_id", -1))

                    partidaDao.update(
                        Partida(
                            id = sharedPreferences.getInt("partida_id", 1),
                            juego = partida.juego,
                            pantalla = partida.pantalla,
                            juegoMapa = currentLocationIndex,
                            hj = partida.hj
                        )
                    )
                }
            }

        } else {
            MapManager.destroy()
            gameManagerService = GameManager.get()
            gameManagerService?.startGame("AMAIERAKO JARDUERA")
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
