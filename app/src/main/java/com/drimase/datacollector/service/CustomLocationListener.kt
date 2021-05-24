package com.drimase.datacollector.service


import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData

private const val TAG = "CustomLocationListener"

class CustomLocationListener  constructor(
    private val location : MutableLiveData<Location>
) : LocationListener, Cloneable{

    private val status = MutableLiveData<Unit>()

    override fun onLocationChanged(p0: Location) {
        location.value = p0
        Log.i(TAG, p0.toString())
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        Log.d(TAG, status.toString())
    }

    override fun onProviderEnabled(provider: String) {
        Log.d(TAG, "onProviderEnabled: $provider")
        status.value=Unit
    }

    override fun onProviderDisabled(provider: String) {
        Log.d(TAG, "onProviderDisabled: $provider")
        status.value=Unit
    }

    fun getStatus() = status

}