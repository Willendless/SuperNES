package com.willendless.nes.emulator.ppu

@kotlin.ExperimentalUnsignedTypes
object ScrollReg {
    var scroll_x: UByte = 0u
    var scroll_y: UByte = 0u
    var latch = false

    fun write(data: UByte) {
        if (!latch) {
            scroll_x = data
        } else {
            scroll_y = data
        }
        latch = !latch
    }

    fun clear() {
        scroll_x = 0u
        scroll_y = 0u
        latch = false
    }
}
