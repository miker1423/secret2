package com.watsalacanoa.secretv2.services

import android.content.Context
import android.location.LocationManager
import com.watsalacanoa.secretv2.models.Point

class LocationService(context:Context) {
    private val locManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    fun getCurrentLocation() : Point = try {
        val location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        Point(location.latitude, location.longitude)
    } catch (ex:SecurityException) {
        Point(0.0,0.0)
    }
}