package com.willendless.nes.emulator.cpu

@ExperimentalUnsignedTypes
enum class Flag(val mask: UByte) {
    CARRY(0b0000_0001u),
    ZERO(0b0000_0010u),
    INTERRUPT_DISABLE(0b0000_0100u),
    DECIMAL_MODE(0b0000_1000u),
    BREAK(0b0001_0000u),
    BREAK2(0b0010_0000u),
    OVERFLOW(0b0100_0000u),
    NEGATIVE(0b1000_0000u),
}

@ExperimentalUnsignedTypes
object PStatus {
    private var reg: UByte = 0u

    fun toUByte(): UByte = reg

    operator fun invoke(i: UByte) {
        reg = i
    }

    fun setStatus(i: Flag, b: Boolean) {
        reg = if (b) {
            reg or i.mask
        } else {
            reg and i.mask.inv()
        }
    }

    fun get() = reg

    fun getStatus(i: Flag) = (reg and i.mask) != 0.toUByte()

    // ENSURES: update Z, N flags based on other
    fun updateZN(other: UByte) {
        setStatus(Flag.ZERO, other == 0.toUByte())
        setStatus(Flag.NEGATIVE, (other and 0b1000_0000u) != 0.toUByte())
    }
}