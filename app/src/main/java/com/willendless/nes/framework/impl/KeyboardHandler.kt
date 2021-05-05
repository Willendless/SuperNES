package com.willendless.nes.framework.impl

import android.view.View
import com.willendless.nes.framework.Input.KeyEvent
import com.willendless.nes.framework.Pool

class KeyboardHandler(view: View): View.OnKeyListener {
    val pressedKeys = Array<Boolean>(128) { false }
    val keyEventPools = Pool<KeyEvent>(100) { KeyEvent() }
    // Buffer unhandled key event
    val keyEventsBuffer = ArrayList<KeyEvent>()
    val keyEvents = ArrayList<KeyEvent>()
    init {
        view.setOnKeyListener(this)
        view.isFocusableInTouchMode = true
        view.requestFocus()
    }

    override fun onKey(v: View?, keyCode: Int, event: android.view.KeyEvent?): Boolean {
        if (event != null) {
            synchronized (this) {
                val keyEvent = keyEventPools.newObject().apply {
                    this.keyCode = keyCode
                    this.KeyChar = event.unicodeChar.toChar()
                    when (event.action) {
                        android.view.KeyEvent.ACTION_DOWN -> {
                            this.type = KEY_DOWN
                            if (keyCode > 0 && keyCode < 127)
                                pressedKeys[keyCode] = true
                        }
                        android.view.KeyEvent.ACTION_UP -> {
                            this.type = KEY_UP
                            if (keyCode > 0 && keyCode < 127)
                                pressedKeys[keyCode] = false
                        }
                    }
                }
                keyEventsBuffer.add(keyEvent)
            }
        }
        return false
    }

    fun isKeyPressed(keyCode: Int) = synchronized(this) {
        if (keyCode < 0 || keyCode > 127) false
        else pressedKeys[keyCode]
    }

    // REQUIRES: all events in the keyEvents should have been handled and safely to free them
    // ENSURES: getKeyEvents should return a new unhandled key events list and buffered key events list will be cleared
    fun getKeyEvents(): List<KeyEvent> {
        synchronized(this) {
            val len = keyEvents.size
            var i = 0
            while (i < len) {
                keyEventPools.free(keyEvents[i])
                i += 1
            }
            keyEvents.clear()
            keyEvents.addAll(keyEventsBuffer)
            keyEventsBuffer.clear()
            return keyEvents
        }
    }
}