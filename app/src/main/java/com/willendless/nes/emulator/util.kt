package com.willendless.nes.emulator

import java.lang.Exception

inline fun unreachable(s: String): Nothing = throw Exception("code should not reach here:$s")

inline fun getUnsignedByte(value: Byte) = value.toUInt() and 0xFFu

inline fun getUnsignedByte(value: UByte) = value.toUInt()

inline fun regVal(reg: Int) = reg.toByte()
