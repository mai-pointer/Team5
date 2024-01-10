package com.example.didaktikapp

import android.annotation.SuppressLint
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine

class LocationProvider(){
   @SuppressLint("MissingPermission")
   fun getUserLocation (context: Context): Location{
       //val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
       val locationManager :LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
       val isGPSEnabled :Boolean = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
       val posToReturn: Location
       /*if(!isGPSEnabled){
           return null
       }*/

       runBlocking {
           val myPos: Deferred<Location?> = async {
               return@async locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
           }
           posToReturn = myPos.await()!!
       }

       return posToReturn
       /*val myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
       return myLocation*/

       /*return suspendCancellableCoroutine { cont ->
           fusedLocationProviderClient.lastLocation.apply{
               Log.e("ClientProvider", "0 ")
               if(isComplete){
                   if(isSuccessful) {
                       Log.e("ClientProvider", "1 " + result.toString())
                       cont.resume(result){}
                   }else{
                       Log.e("ClientProvider", "2 ")
                       cont.resume(null){}
                   }
                   Log.e("ClientProvider", "3")
                   return@suspendCancellableCoroutine
               }
               addOnSuccessListener {
                   Log.e("ClientProvider", "4 " + it.toString())
                   cont.resume(it){}
               }
               addOnFailureListener {
                   Log.e("ClientProvider", "5 ")
                   cont.resume(null){it}
               }
          }
       }*/
   }


}