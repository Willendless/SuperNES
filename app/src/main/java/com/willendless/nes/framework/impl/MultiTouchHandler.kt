package com.willendless.nes.framework.impl

import android.view.MotionEvent
import android.view.View
import com.willendless.nes.framework.Input.TouchEvent
import com.willendless.nes.framework.Pool

// **pointer** identifier uniquely identifies
//      one instance of a pointer touching the screen
// **pointer index** index to internal array of
//      the MotionEvent that holds the coordinates
// MotionEvent.getPointerIdentifier(int pointerIndex)
// int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)
//                      >> MotionEvent.ACTION_POINTER_INDEX_SHIFT
class MultiTouchHandler(view: View, val scaleX: Float, val scaleY: Float): TouchHandler {
    private val MAX_TOUCHPOINT = 10

    private val isTouched = Array<Boolean>(MAX_TOUCHPOINT) { false }
    private val touchX = Array<Int>(MAX_TOUCHPOINT) { 0 }
    private val touchY = Array<Int>(MAX_TOUCHPOINT) { 0 }
    private val id = Array<Int>(MAX_TOUCHPOINT) { 0 }
    private val touchEventPool = Pool<TouchEvent>(100) { TouchEvent() }
    private val touchEvents = ArrayList<TouchEvent>()
    private val touchEventsBuffer = ArrayList<TouchEvent>()

    init {
        view.setOnTouchListener(this)
    }

    override fun isTouchDown(pointer: Int): Boolean = synchronized(this) {
        isTouched[pointer]
    }

    override fun getTouchX(pointer: Int): Int = synchronized(this) {
        val index = getIndex(pointer)
        if (index < 0 || index >= MAX_TOUCHPOINT) 0
        else touchX[index]
    }

    override fun getTouchY(pointer: Int): Int = synchronized(this) {
        val index = getIndex(pointer)
        if (index < 0 || index >= MAX_TOUCHPOINT) 0
        else touchY[index]
    }

    override fun getTouchEvents(): List<TouchEvent> = synchronized(this) {
        // free current touchEvents
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

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event != null) {
            synchronized(this) {
                val action = event.action and MotionEvent.ACTION_MASK
                val pointerIndex = (event.action and MotionEvent.ACTION_POINTER_INDEX_MASK) shr MotionEvent.ACTION_POINTER_INDEX_SHIFT
                val pointerCnt = event.pointerCount
                for (i in 0 until MAX_TOUCHPOINT) {
                    if (i >= pointerCnt) {
                        isTouched[i] = false
                        id[i] = -1
                        continue
                    }
                    val pointerId = event.getPointerId(i)
                    if (event.action != MotionEvent.ACTION_MOVE && i != pointerIndex) continue
                    when (action) {
                        MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                            val touchEvent = touchEventPool.newObject()
                            touchEvent.type = TouchEvent.TOUCH_DOWN
                            touchEvent.pointer = pointerId
                            touchX[i] = (event.getX(i) * scaleX).toInt()
                            touchY[i] = (event.getY(i) * scaleY).toInt()
                            touchEvent.x = touchX[i]
                            touchEvent.y = touchY[i]
                            isTouched[i] = true
                            id[i] = pointerId
                            touchEventsBuffer.add(touchEvent)
                        }
                        MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL -> {
                            val touchEvent = touchEventPool.newObject()
                            touchEvent.type = TouchEvent.TOUCH_UP
                            touchEvent.pointer = pointerId
                            touchX[i] = (event.getX(i) * scaleX).toInt()
                            touchY[i] = (event.getY(i) * scaleY).toInt()
                            touchEvent.x = touchX[i]
                            touchEvent.y = touchY[i]
                            isTouched[i] = true
                            id[i] = pointerId
                            touchEventsBuffer.add(touchEvent)
                        }
                        MotionEvent.ACTION_MOVE -> {
                            val touchEvent = touchEventPool.newObject()
                            touchEvent.type = TouchEvent.TOUCH_DRAGGED
                            touchEvent.pointer = pointerId
                            touchX[i] = (event.getX(i) * scaleX).toInt()
                            touchY[i] = (event.getY(i) * scaleY).toInt()
                            touchEvent.x = touchX[i]
                            touchEvent.y = touchY[i]
                            isTouched[i] = true
                            id[i] = pointerId
                            touchEventsBuffer.add(touchEvent)
                        }
                    }
                }
                return true
            }
        }
        return false
    }

    private fun getIndex(pointerId: Int): Int {
        var i = 0
        while (i < MAX_TOUCHPOINT) {
            if (id[i] == pointerId) return i
            i++
        }
        return -1
    }
}