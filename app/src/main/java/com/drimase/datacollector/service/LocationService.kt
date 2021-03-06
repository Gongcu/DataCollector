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


class LocationService @Inject constructor(
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
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 1f, locationListener)
    }


    private fun observingProviderStatus(){
        locationListener.getStatus().observeForever {
            requestLocation()
        }
    }
}