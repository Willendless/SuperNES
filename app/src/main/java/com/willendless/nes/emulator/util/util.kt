package com.willendless.nes.emulator.util

import android.util.Log
import kotlin.system.exitProcess

inline fun unreachable(s: String): Nothing = throw NESException("code should not reach here:$s")

inline fun assert(predicate: Boolean, msg: String) {
    if (predicate) throw NESException(msg)
}

inline fun nesAssert(predicate: Boolean, msg: String) {
    if (!predicate) {
        Log.e("NES Assertion Failed", msg)
        exitProcess(-1)
    }
}
