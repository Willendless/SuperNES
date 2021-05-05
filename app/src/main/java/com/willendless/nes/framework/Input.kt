package com.willendless.nes.framework

interface Input {
    data class KeyEvent(var type: Int = 0, var keyCode: Int = 0, var KeyChar: Char = '\u0000') {
        val KEY_DOWN = 0
        val KEY_UP = 1
    }
    data class TouchEvent(val type: Int, val x: Int, val y: Int, val pointer: Int) {
        val TOUCH_DOWN = 0
        val TOUCH_UP = 1
        val TOUCH_DRAGGED = 2
    }

    fun isKeyPressed(keyCode: Int): Boolean
    fun isTouchDown(pointer: Int): Boolean
    fun getTouchX(pointer: Int): Int
    fun getTouchY(pointer: Int): Int
    fun getAccelX(): Float
    fun getAccelY(): Float
    fun getAccelZ(): Float
    fun getKeyEvents(): List<KeyEvent>
    fun getTouchEvents(): List<TouchEvent>
}
