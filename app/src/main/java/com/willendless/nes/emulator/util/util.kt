package com.willendless.nes.emulator.util

inline fun unreachable(s: String): Nothing = throw NESException("code should not reach here:$s")

inline fun assert(predicate: Boolean, msg: String) {
    if (predicate) throw NESException(msg)
}
