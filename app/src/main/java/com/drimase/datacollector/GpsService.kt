package com.drimase.datacollector

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject

private const val TAG = "GpsService"
private const val MIN_TIME_BW_UPDATES  = 1000
private const val MIN_DISTANCE_CHANGE_FOR_UPDATES = 0
@SuppressLint("MissingPermission")
class GpsService @Inject constructor(
        private val application: BaseApplication
) : Service(),LocationListener{

    //lateinit var location: Location
    val locationLiveData: MutableLiveData<Location> by lazy {
        MutableLiveData<Location>()
    }
    var locationManager : LocationManager = application.getSystemService(LOCATION_SERVICE) as LocationManager


    init {
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        when {
            isGpsEnabled -> {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,1f,this)
            }
            isNetworkEnabled -> {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,1f,this)
            }
            else -> {
                Log.e(TAG,"Location Service Failed")
            }
        }
    }



    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onLocationChanged(p0: Location) {
        //location = p0
        locationLiveData.value = p0
        Log.i(TAG,p0.toString())
    }
}