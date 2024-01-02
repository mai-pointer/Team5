package com.example.didaktikapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng


class LocationProvider: AppCompatActivity() {

    private val PERMISSIONCODE = 100
    private var isPermissions = false
    private var myLocation: Location = Location("My Location")

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    interface LocationListener {
        fun onLocationChanged(location: Location)
    }

    private var locationListener: LocationListener? = null

    fun setLocationListener(listener: LocationListener) {
        locationListener = listener
        checkPermissions()
    }

    fun checkPermissions() {
        val permissions = arrayListOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }

        val permissionsArray = permissions.toTypedArray()
        if (hasPermissions(permissionsArray)) {
            isPermissions = true
            onPermissionsGranted()
        } else {
            requestPermissions(permissionsArray)
        }
    }

    private fun hasPermissions(permissions: Array<String>): Boolean {
        return permissions.all {
            return ContextCompat.checkSelfPermission(
                this,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun onPermissionsGranted() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    myLocation.latitude = it.latitude
                    myLocation.longitude = it.longitude
                } else {
                    Toast.makeText(this, "No se puede obtener la ubicacion", Toast.LENGTH_SHORT).show()
                }
            }

            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                30000
            ).apply {
                setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                setWaitForAccurateLocation(true)
            }.build()

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    super.onLocationResult(p0)

                    for (location in p0.locations) {
                        myLocation.latitude = location.latitude
                        myLocation.longitude = location.longitude
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (_: SecurityException) {

        }
    }

    private fun requestPermissions(permisos: Array<String>) {
        ActivityCompat.requestPermissions(this, permisos, PERMISSIONCODE)
    }

    fun provideLocation(): Location {
        return myLocation
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == PERMISSIONCODE) {
            val allPermissionsGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }

            if (grantResults.isNotEmpty() && allPermissionsGranted) {
                isPermissions = true
                onPermissionsGranted()
            }
        }
    }

}