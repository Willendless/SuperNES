package com.willendless.nes.emulator.ppu

enum class Flag(val mask: UByte) {
    IGNORE_RAM_WRITE(0b0001_0000u),
    SCANLINE_SPRITE_CNT(0b0010_0000u),
    PIXEL_OVERLAP(0b0100_0000u),
    VBLANK(0b1000_0000u)
}

object StatusReg {
    var reg: UByte = 0u
    fun get() = reg
}