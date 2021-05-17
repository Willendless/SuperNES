package com.willendless.nes.emulator.ppu

@ExperimentalUnsignedTypes
object AddressReg {
    val value = Pair<UByte, UByte>(0u, 0u)
    var waitLo = false

    fun update(data: UByte) {
        if (!waitLo) {
            value.copy(first = data and 0x3Fu)
        } else {
            value.copy(second = data)
        }
        waitLo = !waitLo
    }

    fun inc(other: UByte) {
        val res = get() + other
        value.copy(first = (res shr 8).toUByte(), second = (res and 0xFFu).toUByte())
    }

    fun get(): UShort = (value.first.toInt() shl 8).toUShort() or value.second.toUShort()
}