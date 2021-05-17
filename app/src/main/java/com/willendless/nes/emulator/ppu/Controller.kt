package com.willendless.nes.emulator.ppu

import android.service.controls.Control

@kotlin.ExperimentalUnsignedTypes
enum class Flag(val mask: UByte) {
    NAMETABLE1(0b0000_0001u),
    NAMETABLE2(0b0000_0010u),
    VRAM_ADD_AUTOINCREMENT(0b0000_0100u),
    SPRITE_TABLE_BASE(0b0000_1000u),
    BACKGROUND_TABLE_BASE(0b0001_0000u),
    SPRITE_SIZE(0b0010_0000u),
    PPU_SLAVE_SELECT(0b0100_0000u),
    NMI_ENABLE(0b1000_0000u)
}

@kotlin.ExperimentalUnsignedTypes
data class Controller(var reg: UByte = 0u.toUByte()) {
    fun set(i: UByte) {
        reg = i
    }

    fun getFlag(flag: Flag): Boolean = reg and flag.mask != 0u.toUByte()
}