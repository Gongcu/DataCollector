package com.drimase.datacollector.service

import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.drimase.datacollector.base.BaseApplication
import javax.inject.Inject
import kotlin.math.round

class SensorManager @Inject constructor(
    application: BaseApplication,
    private val altitude : MutableLiveData<Float>
) : SensorEventListener{
    private val sensorManager = application.getSystemService(SENSOR_SERVICE) as SensorManager
    private val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)

    init {
        Log.d("Sensor", "onInit: Sensor")
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)

    }

    override fun onSensorChanged(event: SensorEvent) {
        val pressure = event.values[0]
        var height = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure)
        height = round(height*10)/10
        altitude.postValue(height)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

}