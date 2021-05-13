package com.willendless.nes

import com.willendless.nes.emulator.Memory
import org.junit.Assert
import org.junit.Test

@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class MemoryTest {
    private val memory = Memory()

    @Test fun test_read_write_byte() {
        memory.writeUByte(0x0u, 0x42u)
        Assert.assertEquals(0x42u.toUByte(), memory.readUByte(0u))
    }

    @Test fun test_read_write_short() {
        memory.writeUShort(0xFFFCu, 0xCAFEu)
        Assert.assertEquals(0xCAFEu.toUShort(), memory.readUShort(0xFFFCu))
    }

    @Test fun test_read_write_half_short() {
        memory.writeUShort(0xFFFCu, 0x00FFu)
        Assert.assertEquals(0x00FFu.toUShort(), memory.readUShort(0xFFFCu))
    }
}