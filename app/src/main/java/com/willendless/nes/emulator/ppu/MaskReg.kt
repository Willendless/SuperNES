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
    GREYSCALE(0b0000_0001u),
    LEFTMOST_8PXL_BACKGROUND(0b0000_0010u),
    LEFTMOST_8PXL_SPRITE(0b0000_0100u),
    SHOW_BACKGROUND(0b0000_1000u),
    SHOW_SPRITES(0b0001_0000u),
    EMPHASISE_RED(0b0010_0000u),
    EMPHASISE_GREEN(0b0100_0000u),
    EMPHASISE_BLUE(0b1000_0000u),
}

@kotlin.ExperimentalUnsignedTypes
object MaskReg {
    var reg: UByte = 0u.toUByte()
    fun set(i: UByte) {
        reg = i
    }

    fun is_grayscale(): Boolean {
        return reg.and(MaskRegFlag.GREYSCALE.mask) != 0.toUByte()
    }

    fun leftmost_8pxl_background(): Boolean {
        return reg.and(MaskRegFlag.LEFTMOST_8PXL_BACKGROUND.mask) != 0.toUByte()
    }

    fun leftmost_8pxl_sprite(): Boolean {
        return reg.and(MaskRegFlag.LEFTMOST_8PXL_SPRITE.mask) != 0.toUByte()
    }

    fun show_background(): Boolean {
        return reg.and(MaskRegFlag.SHOW_BACKGROUND.mask) != 0.toUByte()
    }

    fun show_sprites(): Boolean {
        return reg.and(MaskRegFlag.SHOW_SPRITES.mask) != 0.toUByte()
    }
}
