package com.willendless.nes.emulator.ppu

// 7  bit  0
// ---- ----
// BGRs bMmG
// |||| ||||
// |||| |||+- Greyscale (0: normal color, 1: produce a greyscale display)
// |||| ||+-- 1: Show background in leftmost 8 pixels of screen, 0: Hide
// |||| |+--- 1: Show sprites in leftmost 8 pixels of screen, 0: Hide
// |||| +---- 1: Show background
// |||+------ 1: Show sprites
// ||+------- Emphasize red (green on PAL/Dendy)
// |+-------- Emphasize green (red on PAL/Dendy)
// +--------- Emphasize blue

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
