package com.willendless.nes.emulator.ppu

@kotlin.ExperimentalUnsignedTypes
enum class ControlReg1Flag(val mask: UByte) {
    NAME_TABLE_ADDR1(0b0000_0001u),
    NAME_TABLE_ADDR2(0b0000_0010u),
    VRAM_ADD_AUTOINCREMENT(0b0000_0100u),
    SPRITE_TABLE_BASE(0b0000_1000u),
    BACKGROUND_TABLE_BASE(0b0001_0000u),
    SPRITE_SIZE(0b0010_0000u),
    PPU_SLAVE_SELECT(0b0100_0000u),
    NMI_ENABLE(0b1000_0000u)
}

@kotlin.ExperimentalUnsignedTypes
object ControlReg1 {
    private var reg: UByte = 0u.toUByte()
    fun set(i: UByte) {
        reg = i
    }

    fun get() = reg

    fun getFlag(flag: ControlReg1Flag): Boolean = reg and flag.mask != 0u.toUByte()
}

@kotlin.ExperimentalUnsignedTypes
enum class ControlReg2Flag(val mask: UByte) {
    MONOCHROME_MODE(0b0000_0000u),
    CLIP_BACKGROUND(0b0000_0001u),
    SPRITES(0b0000_0010u),
    DISPLAY_BACKGROUND(0b0000_1000u),
    DISPLAY_SPRITES(0b0001_0000u),
    BACKGROUND_COLOR_INTENSITY(0b1110_0000u)
}

@kotlin.ExperimentalUnsignedTypes
object ControlReg2 {
    var reg: UByte = 0u.toUByte()
    fun set(i: UByte) {
        reg = i
    }
}
