package com.willendless.nes.framework.impl

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.KITKAT)
class CompassHandler(context: Context): SensorEventListener {
    var yaw = 0F
    var pitch = 0F
    var roll = 0F

    init {
        val manager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also { magneticField ->
            manager.registerListener(
                this,
                magneticField,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // do nothing here
    }

    override fun onSensorChanged(event: SensorEvent?) {
        yaw = event?.values?.get(0) ?: yaw
        pitch = event?.values?.get(1) ?: pitch
        roll = event?.values?.get(2) ?: roll
    }

}