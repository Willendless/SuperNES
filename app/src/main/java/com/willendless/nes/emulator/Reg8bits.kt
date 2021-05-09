package com.willendless.nes.emulator

@ExperimentalUnsignedTypes
data class Reg8bits(var reg: UByte = 0u) {
    constructor(v: Int) : this(v.toUByte()) {}

    // ENSURE: wrap around plus
    operator fun inc(): Reg8bits = Reg8bits((reg + 1u).toUByte())

    // ENSURE: wrap around sub
    operator fun dec(): Reg8bits = Reg8bits((reg - 1u).toUByte())

    // ENSURE: wrap around plus
    operator fun plus(operand: UInt): Int = (reg + operand).toUByte().toInt()
    operator fun plus(operand: Int): Int = (reg.toInt() + operand).toUByte().toInt()

    fun getValUnsigned(): Int = reg.toInt()
    fun getValSigned(): Int = reg.toByte().toInt()

    fun set(value: UInt) {
        reg = value.toUByte()
    }

    // requires: value <= 0xFF
    fun set(value: Int) {
        reg = value.toUByte()
    }
}