package com.willendless.nes.emulator.util

inline fun unreachable(s: String): Nothing = throw NESException("code should not reach here:$s")

inline fun getUnsignedByte(value: Byte) = value.toUInt() and 0xFFu

inline fun getUnsignedByte(value: UByte) = value.toUInt()

inline fun regVal(reg: Int) = reg.toByte()
