package com.willendless.nes.emulator.ppu

@kotlin.ExperimentalUnsignedTypes
enum class MaskRegFlag(val mask: UByte) {
    MONOCHROME_MODE(0b0000_0000u),
    CLIP_BACKGROUND(0b0000_0001u),
    SPRITES(0b0000_0010u),
    DISPLAY_BACKGROUND(0b0000_1000u),
    DISPLAY_SPRITES(0b0001_0000u),
    BACKGROUND_COLOR_INTENSITY(0b1110_0000u)
}

@kotlin.ExperimentalUnsignedTypes
object MaskReg {
    var reg: UByte = 0u.toUByte()
    fun set(i: UByte) {
        reg = i
    }
}
