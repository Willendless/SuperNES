package com.willendless.nes.emulator

@ExperimentalUnsignedTypes
data class PC(var reg: UShort = 0u) {
    operator fun inc(): PC = PC((reg + 1u).toUShort())
    operator fun plus(operand: UInt): PC = PC((reg + operand).toUShort())

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