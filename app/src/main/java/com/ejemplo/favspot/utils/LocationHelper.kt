package com.ejemplo.favspot.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import com.google.android.gms.location.LocationServices
import android.location.Location
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat

import androidx.core.content.ContextCompat.getSystemService
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object LocationHelper {

    suspend fun getCurrentLocation(context: Context): Pair<Double, Double>? {
        val permissionFine =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val permissionCoarse =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (permissionFine != PackageManager.PERMISSION_GRANTED && permissionCoarse != PackageManager.PERMISSION_GRANTED) {
            return null // No hay permisos
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        return suspendCancellableCoroutine { cont ->
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        cont.resume(Pair(location.latitude, location.longitude))
                    } else {
                        cont.resume(null)
                    }
                }
                .addOnFailureListener {
                    cont.resume(null)
                }
        }
    }
}