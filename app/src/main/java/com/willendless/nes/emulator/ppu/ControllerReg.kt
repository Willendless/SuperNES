package com.willendless.nes.emulator.ppu

import com.willendless.nes.emulator.util.unreachable
import java.net.URLStreamHandler

// 7  bit  0
// ---- ----
// VPHB SINN
// |||| ||||
// |||| ||++- Base nametable address
// |||| ||    (0 = $2000; 1 = $2400; 2 = $2800; 3 = $2C00)
// |||| |+--- VRAM address increment per CPU read/write of PPUDATA
// |||| |     (0: add 1, going across; 1: add 32, going down)
// |||| +---- Sprite pattern table address for 8x8 sprites
// ||||       (0: $0000; 1: $1000; ignored in 8x16 mode)
// |||+------ Background pattern table address (0: $0000; 1: $1000)
// ||+------- Sprite size (0: 8x8 pixels; 1: 8x16 pixels)
// |+-------- PPU master/slave select
// |          (0: read backdrop from EXT pins; 1: output color on EXT pins)
// +--------- Generate an NMI at the start of the
// vertical blanking interval (0: off; 1: on)

@kotlin.ExperimentalUnsignedTypes
enum class ControllerRegFlag(val mask: UByte) {
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
object ControllerReg {
    private const val NAME_TABLE_BASE0: UShort = 0x2000u
    private const val NAME_TABLE_BASE1: UShort = 0x2400u
    private const val NAME_TABLE_BASE2: UShort = 0x2800u
    private const val NAME_TABLE_BASE3: UShort = 0x2C00u

    private const val PATTERN_TABLE_BASE0: UShort = 0x0u
    private const val PATTERN_TABLE_BASE1: UShort = 0x1000u

    private var reg: UByte = 0u.toUByte()
    fun set(i: UByte) {
        reg = i
    }

    fun get() = reg

    fun getFlag(flag: ControllerRegFlag): Boolean = reg and flag.mask != 0u.toUByte()

    fun getNameTableBase(): UShort {
        val lo = if (getFlag(ControllerRegFlag.NAME_TABLE_ADDR1)) 1 else 0
        val hi = if (getFlag(ControllerRegFlag.NAME_TABLE_ADDR2)) 1 else 0
        return when (hi * 2 + lo) {
            0 -> NAME_TABLE_BASE0
            1 -> NAME_TABLE_BASE1
            2 -> NAME_TABLE_BASE2
            3 -> NAME_TABLE_BASE3
            else -> unreachable("Unknown nametable base flag")
        }
    }

    enum class PatternTableKind {
        BACKGROUND,
        SPRITE
    }

    fun getPatternTableBase(patternTableKind: PatternTableKind): UShort {
        val backgroundBase = getFlag(ControllerRegFlag.BACKGROUND_TABLE_BASE)
        val spriteBase = getFlag(ControllerRegFlag.SPRITE_TABLE_BASE)
        return when(patternTableKind) {
            PatternTableKind.BACKGROUND -> if (backgroundBase) PATTERN_TABLE_BASE1 else PATTERN_TABLE_BASE0
            PatternTableKind.SPRITE -> if (spriteBase) PATTERN_TABLE_BASE1 else PATTERN_TABLE_BASE0
        }
    }

    fun getBackgroundTableBase(): UShort = if (getFlag(ControllerRegFlag.BACKGROUND_TABLE_BASE)) {
        PATTERN_TABLE_BASE1
    } else {
        PATTERN_TABLE_BASE0
    }
}

