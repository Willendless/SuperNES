package com.willendless.nes.emulator

import android.util.Log

@kotlin.ExperimentalUnsignedTypes
object Joypads {
    enum class JoypadButtonFlag(val mask: UByte) {
        RIGHT(0b1000_0000u),
        LEFT(0b0100_0000u),
        DOWN(0b0010_0000u),
        UP(0b0001_0000u),
        START(0b0000_1000u),
        SELECT(0b0000_0100u),
        BUTTON_B(0b0000_0010u),
        BUTTON_A(0b0000_0001u);
    }

    private var strobeMode: Boolean = false
    private var buttonIndex: UByte = 0u
    var buttonStatus: UByte = 0u

    fun setStatus(flag: JoypadButtonFlag, value: Boolean) {
        buttonStatus = if (value) buttonStatus or flag.mask
        else buttonStatus xor flag.mask
    }

    fun reset() {
        buttonStatus = 0u
    }

    // Write by NESBus from CPU
    // write byte whose first bit is 1 -> reset buttonIndex
    // write byte whose first bit is 0 -> begin cycle reads
    fun write(data: UByte) {
        strobeMode = (data and 1u) == 1u.toUByte()
        if (strobeMode)
            buttonIndex = 0u
    }

    // Read by NESBus from CPU
    // strobe mode on -> only report button A
    // strobe mode off -> cycles through all buttons then report 1s
    fun read(): UByte {
        if (buttonIndex > 7u) {
            return 1u
        }

        val response = (buttonStatus and (1 shl buttonIndex.toInt()).toUByte()).toInt() shr
                buttonIndex.toInt()

        if (!strobeMode && buttonIndex <= 7u)
            buttonIndex++

        return response.toUByte()
    }
}