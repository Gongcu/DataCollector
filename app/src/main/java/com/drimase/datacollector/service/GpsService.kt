package com.drimase.datacollector.service

import android.Manifest
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.location.LocationManagerCompat
import androidx.lifecycle.MutableLiveData
import com.drimase.datacollector.base.BaseApplication
import javax.inject.Inject


class GpsService @Inject constructor(
        private val application: BaseApplication,
        private val location: MutableLiveData<Location>
) {

    private var locationListener: CustomLocationListener = CustomLocationListener(location)
    private val locationManager = application.getSystemService(LOCATION_SERVICE) as LocationManager
    private var firstInit = true

    init {
        requestLocation()
        observingProviderStatus()
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
            Toast.makeText(application, "위치 서비스 이용이 불가능합니다", Toast.LENGTH_SHORT).show()
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


        return if(gpsLocation == null && networkLocation==null){
            if(isNetworkLocationEnabled(locationManager))
                LocationManager.NETWORK_PROVIDER
            else if(isGpsLocationEnabled(locationManager))
                LocationManager.GPS_PROVIDER
            else
                locationManager.getBestProvider(criteria, true)
        }else if(gpsLocation == null || !isGpsLocationEnabled(locationManager))
            LocationManager.NETWORK_PROVIDER
        else if(networkLocation == null || !isNetworkLocationEnabled(locationManager))
            LocationManager.GPS_PROVIDER
        else
            LocationManager.PASSIVE_PROVIDER
    }


    private fun isGpsLocationEnabled(locationManager: LocationManager): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
    private fun isNetworkLocationEnabled(locationManager: LocationManager): Boolean {
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun observingProviderStatus(){
        locationListener.getStatus().observeForever {
            requestLocation()
        }
    }

    companion object{
        const val TAG = "GpsService"
    }

}