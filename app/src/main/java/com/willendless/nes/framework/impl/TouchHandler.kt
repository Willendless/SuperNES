package com.willendless.nes.framework.impl

import android.view.View
import com.willendless.nes.framework.Input

interface TouchHandler: View.OnTouchListener {
    fun isTouchDown(pointer: Int): Boolean
    fun getTouchX(pointer: Int): Int
    fun getTouchY(pointer: Int): Int
    fun getTouchEvents(): List<Input.TouchEvent>
}