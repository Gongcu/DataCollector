package com.drimase.datacollector

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject

private const val TAG = "GpsService"

class GpsService @Inject constructor(
        private val application: BaseApplication
) : Service(), LocationListener {

    //lateinit var location: Location
    val locationLiveData: MutableLiveData<Location> by lazy {
        MutableLiveData<Location>()
    }
    var locationManager: LocationManager = application.getSystemService(LOCATION_SERVICE) as LocationManager


    init {
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)



        if (ContextCompat.checkSelfPermission(application, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(application, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED)
            when {
                isGpsEnabled -> {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 1f, this)
                }
                isNetworkEnabled -> {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 1f, this)
                }
                else -> {
                    Log.e(TAG, "Location Service Failed")
                }
            }
    }


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onLocationChanged(p0: Location) {
        //location = p0
        locationLiveData.value = p0
        Log.i(TAG, p0.toString())
    }

    override fun onProviderEnabled(provider: String) {
        super.onProviderEnabled(provider)
    }

    override fun onProviderDisabled(provider: String) {
        super.onProviderDisabled(provider)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }
}