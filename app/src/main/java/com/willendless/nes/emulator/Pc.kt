package com.willendless.nes.emulator

data class Pc(var reg: UShort = 0u) {
    operator fun inc(): Pc = Pc((reg + 1u).toUShort())
    operator fun plus(operand: UInt): Pc = Pc((reg + operand).toUShort())

    fun getValUnsigned(): Int {
        return reg.toInt()
    }

    // requires; value <= 0xFFFF
    fun set(value: UInt) {
        reg = value.toUShort()
    }

    // requires; value <= 0xFFFF
    fun set(value: Int) {
        reg = value.toUShort()
    }
}