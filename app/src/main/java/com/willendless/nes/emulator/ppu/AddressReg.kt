package com.willendless.nes.emulator.ppu

@ExperimentalUnsignedTypes
object AddressReg {
    private val value = ubyteArrayOf(0x0u, 0x0u) // [high, low]
    private var waitLo = false

    fun update(data: UByte) {
        if (!waitLo) {
            value[0] = data and 0x3Fu
        } else {
            value[1] = data
        }
        waitLo = !waitLo
    }

    fun inc(other: UByte) {
        var res = get() + other
        if (res > 0x3FFFu)
            res = 0x2000u
        value[0] = ((res shr 8) and 0x3Fu).toUByte()
        value[1] = (res and 0xFFu).toUByte()
    }

    fun get(): UShort = (value[0].toInt() shl 8).toUShort() or value[1].toUShort()
}