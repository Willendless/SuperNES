package com.willendless.nes

import org.junit.Test
import com.willendless.nes.emulator.ppu.ScrollReg

import org.junit.Assert.*

@kotlin.ExperimentalUnsignedTypes
class ScrollRegTest {
    @Test
    fun init_correct() {
        ScrollReg.write(0xAu)
        ScrollReg.write(0xBu)
        assertEquals(ScrollReg.scroll_x, 0xA.toUByte())
        assertEquals(ScrollReg.scroll_y, 0xB.toUByte())
    }
}