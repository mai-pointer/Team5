package com.example.didaktikapp

import android.annotation.SuppressLint
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine

class LocationProvider{
   @SuppressLint("MissingPermission")
   suspend fun getUserLocation (context: Context): Location?{
       val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
       val isUserLocationPermissionsGranted = true
       val locationManager :LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
       val isGPSEnabled :Boolean = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

       if(!isGPSEnabled || !isUserLocationPermissionsGranted){
           return null
       }

       return suspendCancellableCoroutine { cont ->
           fusedLocationProviderClient.lastLocation.apply{
               if(isComplete){
                   if(isSuccessful) {
                       cont.resume(result) {}
                   }else{
                       cont.resume(null){}
                   }
                   return@suspendCancellableCoroutine
               }
               addOnSuccessListener {
                   cont.resume(it){}
               }
               addOnFailureListener {
                   cont.resume(null){it}
               }
           }
       }
   }

    private fun checkPermissions(context: Context){
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            //TODO: llamar a las funciones requestPermission
        }else{

        }
    }

    fun requestLocationPermision(activity: Activity){
        if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)){

        }else{
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 777)
        }
    }


}