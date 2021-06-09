package com.willendless.nes

import org.junit.Test
import com.willendless.nes.emulator.ppu.Rect

import org.junit.Assert.*

class RectTest {
    @Test
    fun init_correct() {
        val rect = Rect(1, 2, 3, 4)
        assertEquals(1, rect.x1)
        assertEquals(2, rect.y1)
        assertEquals(3, rect.x2)
        assertEquals(4, rect.y2)
    }
}