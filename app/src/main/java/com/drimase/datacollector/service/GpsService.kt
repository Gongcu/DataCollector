package com.drimase.datacollector.service

import android.Manifest
import android.app.Application
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.drimase.datacollector.BaseApplication
import javax.inject.Inject

private const val TAG = "GpsService"

class GpsService @Inject constructor(
        private val application: BaseApplication,
        private val location : MutableLiveData<Location>
) : LifecycleService(){

    private var locationListener: CustomLocationListener = CustomLocationListener(location)
    private val locationManager = application.getSystemService(LOCATION_SERVICE) as LocationManager
    private var firstInit = true

    init {
        requestLocation()
        observingProviderStatus()
    }

    private fun observingProviderStatus(){
        locationListener.getStatus().observe(this,{
            requestLocation()
        })
    }

    private fun requestLocation(){
        if(firstInit)
            firstInit = !firstInit
        else {
            locationManager.removeUpdates(locationListener)
            locationListener = CustomLocationListener(location)
        }
        val currentProvider = getBestProvider()
        Log.d(TAG, "Location Provider:${currentProvider}")
        if (ActivityCompat.checkSelfPermission(
                application,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                application,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        if(currentProvider!=null)
            locationManager!!.requestLocationUpdates(currentProvider, 3000, 1f, locationListener)
        else
            Toast.makeText(application,"위치 서비스 이용이 불가능합니다",Toast.LENGTH_SHORT).show()
    }

    private fun getBestProvider() : String?{
        var networkLocation : Location? = null
        var gpsLocation : Location? = null
        val criteria = Criteria().apply {
            powerRequirement = Criteria.POWER_LOW
            accuracy = Criteria.ACCURACY_FINE
            isSpeedRequired = true
            isAltitudeRequired = false
            isBearingRequired = false
            isCostAllowed = false
        }

        if (ActivityCompat.checkSelfPermission(
                application,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                application,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        }

        if(gpsLocation == null && networkLocation==null){
            return locationManager!!.getBestProvider(criteria,true)
        }else if(networkLocation == null)
            return LocationManager.GPS_PROVIDER
        else
            return LocationManager.NETWORK_PROVIDER
    }

}