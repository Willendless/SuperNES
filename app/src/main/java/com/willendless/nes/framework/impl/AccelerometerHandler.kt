package com.willendless.nes.framework.impl

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class AccelerometerHandler(context: Context): SensorEventListener {
    var accelX: Float = 0F
    var accelY: Float = 0F
    var accelZ: Float = 0F

    init {
        val manager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        if (manager.getSensorList(Sensor.TYPE_ACCELEROMETER).size != 0) {
            val sensor = manager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0)
            manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // do nothing here
    }

    override fun onSensorChanged(event: SensorEvent?) {
        accelX = event?.values?.get(0) ?: accelX
        accelY = event?.values?.get(1) ?: accelY
        accelZ = event?.values?.get(2) ?: accelZ
    }

}