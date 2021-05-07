package com.willendless.nes.framework.impl

import android.view.MotionEvent
import android.view.View
import com.willendless.nes.framework.Input.TouchEvent
import com.willendless.nes.framework.Input.TouchEvent.Companion.TOUCH_DOWN
import com.willendless.nes.framework.Input.TouchEvent.Companion.TOUCH_DRAGGED
import com.willendless.nes.framework.Input.TouchEvent.Companion.TOUCH_UP
import com.willendless.nes.framework.Pool

class SingleTouchHandler(view: View, val scaleX: Float, val scaleY: Float): TouchHandler {
    private val touchEventPool = Pool<TouchEvent>(100) { TouchEvent() }
    private val touchEvents = ArrayList<TouchEvent>()
    private val touchEventsBuffer = ArrayList<TouchEvent>()
    private var isTouched: Boolean = false
    private var touchX: Int = 0
    private var touchY: Int = 0

    init {
        view.setOnTouchListener(this)
    }

    override fun isTouchDown(pointer: Int): Boolean = synchronized(this) {
        if (pointer == 0) isTouched
        else false
    }

    override fun getTouchX(pointer: Int): Int = synchronized(this) {
        touchX
    }

    override fun getTouchY(pointer: Int): Int = synchronized(this) {
        touchY
    }

    // REQUIRES: current touchEvents can be safely freed
    override fun getTouchEvents(): List<TouchEvent> {
        synchronized(this) {
            // free events
            val len = touchEvents.size
            var i = 0
            while (i < len) {
                touchEventPool.free(touchEvents[i])
                i++
            }
            touchEvents.clear()
            // move events from buffer to events
            touchEvents.addAll(touchEventsBuffer)
            touchEventsBuffer.clear()
            return touchEvents
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
       if (event != null) {
           synchronized(this) {
               val touchEvent = touchEventPool.newObject()
               when (event.action) {
                   MotionEvent.ACTION_DOWN -> {
                       touchEvent.type = TOUCH_DOWN
                       isTouched = true
                   }
                   MotionEvent.ACTION_MOVE -> {
                       touchEvent.type = TOUCH_DRAGGED
                       isTouched = true
                   }
                   MotionEvent.ACTION_CANCEL -> {}
                   MotionEvent.ACTION_UP -> {
                       touchEvent.type = TOUCH_UP
                       isTouched = false
                   }
               }
               touchX = (event.x * scaleX).toInt()
               touchY = (event.y * scaleY).toInt()
               touchEvent.x = touchX
               touchEvent.y = touchY
               touchEventsBuffer.add(touchEvent)
           }
       }
       return true
    }
}