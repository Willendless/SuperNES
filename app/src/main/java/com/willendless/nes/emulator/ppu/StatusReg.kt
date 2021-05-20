package com.willendless.nes.emulator.ppu

// 7  bit  0
// ---- ----
// VSO. ....
// |||| ||||
// |||+-++++- Least significant bits previously written into a PPU register
// |||        (due to register not being updated for this address)
// ||+------- Sprite overflow. The intent was for this flag to be set
// ||         whenever more than eight sprites appear on a scanline, but a
// ||         hardware bug causes the actual behavior to be more complicated
// ||         and generate false positives as well as false negatives; see
// ||         PPU sprite evaluation. This flag is set during sprite
// ||         evaluation and cleared at dot 1 (the second dot) of the
// ||         pre-render line.
// |+-------- Sprite 0 Hit.  Set when a nonzero pixel of sprite 0 overlaps
// |          a nonzero background pixel; cleared at dot 1 of the pre-render
// |          line.  Used for raster timing.
// +--------- Vertical blank has started (0: not in vblank; 1: in vblank).
// Set at dot 1 of line 241 (the line *after* the post-render
// line); cleared after reading $2002 and at dot 1 of the
// pre-render line.

@ExperimentalUnsignedTypes
enum class Flag(val mask: UByte) {
    IGNORE_RAM_WRITE(0b0001_0000u),
    SPRITE_OVERFLOW(0b0010_0000u),
    SPRITE_0_HIT(0b0100_0000u),
    VBLANK_STATE(0b1000_0000u)
}

@ExperimentalUnsignedTypes
object StatusReg {
    var reg: UByte = 0u
    fun get() = reg
    fun getStatus(flag: Flag): Boolean = (reg and flag.mask) != 0u.toUByte()
    fun setStatus(flag: Flag, value: Boolean) {
        reg = if (value) reg or flag.mask else reg and flag.mask.inv()
    }
}