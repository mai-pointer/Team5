package com.example.didaktikapp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng

class LocationProvider(context: Context, private val locationCallback: LocationCallback) {
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    init {

    }

    fun startLocationUpdates() {
        // Configura la actualización de la ubicación
        fusedLocationClient.requestLocationUpdates(
            LocationRequest.create(),
            locationCallback,
            null
        )
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    companion object {

        const val LOCATION_PERMISSION_REQUEST_CODE = 1001
        fun calculateDistance(location1: LatLng, location2: LatLng): Float {
            val results = FloatArray(1)
            Location.distanceBetween(
                location1.latitude, location1.longitude,
                location2.latitude, location2.longitude,
                results
            )
            return results[0]
        }
    }
}