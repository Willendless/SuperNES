package com.willendless.nes.framework

interface Input {
    data class TouchEvent(var type: Int = 0, var x: Int = 0, var y: Int = 0, var pointer: Int = 0) {
        companion object {
            val TOUCH_DOWN = 0
            val TOUCH_UP = 1
            val TOUCH_DRAGGED = 2
        }
    }
    data class KeyEvent(var type: Int = 0, var keyCode: Int = 0, var KeyChar: Char = '\u0000') {
        companion object {
            val KEY_DOWN = 0
            val KEY_UP = 1
        }
    }
    data class JoypadEvent(var type: Int = UP, var padDown: Boolean = true) {
        companion object {
            val UP = 0
            val DOWN = 1
            val LEFT = 2
            val RIGHT = 3
            val SELECT = 4
            val START = 5
            val A = 6
            val B = 7
        }
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
    fun getJoypadEvents(): List<JoypadEvent>
}
