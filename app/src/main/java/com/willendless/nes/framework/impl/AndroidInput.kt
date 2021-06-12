package com.willendless.nes.framework.impl

import android.content.Context
import android.view.View
import com.willendless.nes.framework.Input

class AndroidInput(context: Context, view: View, scaleX: Float, scaleY: Float): Input {
    private val accelerometerHandler = AccelerometerHandler(context)
    private val keyboardHandler = KeyboardHandler(view)
    private val touchHandler = MultiTouchHandler(view, scaleX, scaleY)

    override fun isKeyPressed(keyCode: Int): Boolean = keyboardHandler.isKeyPressed(keyCode)

    override fun isTouchDown(pointer: Int): Boolean = touchHandler.isTouchDown(pointer)

    override fun getTouchX(pointer: Int): Int = touchHandler.getTouchX(pointer)

    override fun getTouchY(pointer: Int): Int = touchHandler.getTouchY(pointer)

    override fun getAccelX(): Float = accelerometerHandler.accelX

    override fun getAccelY(): Float = accelerometerHandler.accelY

    override fun getAccelZ(): Float = accelerometerHandler.accelZ

    override fun getKeyEvents(): List<Input.KeyEvent> = keyboardHandler.getKeyEvents()

    override fun getTouchEvents(): List<Input.TouchEvent> = touchHandler.getTouchEvents()

    override fun getJoypadEvents(): List<Input.JoypadEvent> {
        TODO("Not yet implemented")
    }
}