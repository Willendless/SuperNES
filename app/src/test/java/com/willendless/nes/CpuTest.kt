package com.willendless.nes

import com.willendless.nes.emulator.NESBus
import com.willendless.nes.emulator.cpu.CPU
import com.willendless.nes.emulator.cpu.Flag
import com.willendless.nes.emulator.util.NESException
import org.junit.Before
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import java.io.PrintStream

@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class CPUTest {
    private val cpu = CPU

    @Before fun setup() {
        cpu.switchBus(RawBus)
        cpu.bus.clear()
    }

    @After fun tearDown() {
        cpu.switchBus(cpu.bus)
    }

    private fun loadAndRun(program: UByteArray, offset: Int = 0x500, os: PrintStream? = null) {
        CPU.load(program, offset)
        CPU.reset(offset.toUShort())
        CPU.run(os = os)
    }

    @Test fun test_lda_immediate_load_data() {
        val program = ubyteArrayOf(0xa9u, 0x05u, 0x00u)
        loadAndRun(program)
        assertEquals(0x05.toUByte(), cpu.a)
        assertFalse(cpu.status.getStatus(Flag.ZERO))
        assertFalse(cpu.status.getStatus(Flag.NEGATIVE))
    }

    @Test fun test_lda_zero_flag() {
        val program = ubyteArrayOf(0xa9u, 0x00u, 0x00u)
        loadAndRun(program)
        assertTrue(cpu.status.getStatus(Flag.ZERO))
    }

    @Test fun test_lda_zero_page() {
        cpu.bus.writeUByte(0x00u, 0x01u)
        val program = ubyteArrayOf(0xa5u, 0x00u, 0x00u)
        loadAndRun(program)
        assertEquals(1.toUByte(), cpu.a)
    }

    @Test fun test_lda_zero_page_x() {
        cpu.bus.writeUByte(0x01u, 0x01u)
        val program = ubyteArrayOf(0xa2u, 0x01u, 0xb5u, 0x00u, 0x00u)
        loadAndRun(program)
        assertEquals(1.toUByte(), cpu.a)
    }

    @Test fun test_lda_zero_page_x_overflow() {
        cpu.bus.writeUByte(0x00u, 0x02u)
        val program = ubyteArrayOf(0xa2u, 0xffu, 0xb5u, 0x01u, 0x00u)
        loadAndRun(program)
        assertEquals(2.toUByte(), cpu.a)
    }

    @Test fun test_lda_absolute() {
        cpu.bus.writeUShort(0x00u, 0x0008u)
        val program = ubyteArrayOf(0xadu, 0x00u, 0x00u, 0x00u)
        loadAndRun(program)
        assertEquals(8.toUByte(), cpu.a)
    }

    @Test fun test_lda_absolute_x() {
        cpu.bus.writeUShort(0x10u, 0x0009u)
        val program = ubyteArrayOf(0xa2u, 0x10u, 0xbdu, 0x00u, 0x00u, 0x00u)
        loadAndRun(program)
        assertEquals(9.toUByte(), cpu.a)
    }

    @Test fun test_lda_absolute_y() {
        cpu.bus.writeUShort(0x10u, 0x0009u)
        val program = ubyteArrayOf(0xa0u, 0x10u, 0xb9u, 0x00u, 0x00u, 0x00u)
        loadAndRun(program)
        assertEquals(9.toUByte(), cpu.a)
    }

    @Test fun test_lda_indirect_x() {
        cpu.bus.writeUShort(0x00u, 0x0010u)
        cpu.bus.writeUShort(0x10u, 0x0010u)
        val program = ubyteArrayOf(0xa2u, 0x01u, 0xa1u, 0xffu, 0x00u)
        loadAndRun(program)
        assertEquals(0x10.toUByte(), cpu.a)
    }

    @Test fun test_lda_indirect_y() {
        cpu.bus.writeUShort(0xBBu, 0x1122u)
        cpu.bus.writeUShort(0x1124u, 0x00AAu)
        val program = ubyteArrayOf(0xa0u, 0x02u, 0xb1u, 0xbbu, 0x00u)
        loadAndRun(program)
        assertEquals(0xaa.toUByte(), cpu.a)
    }

    @Test fun test_sta_zero_page() {
        val program = ubyteArrayOf(0xa9u, 0x02u, 0x85u, 0x01u, 0x00u)
        loadAndRun(program)
        assertEquals(0x02.toUByte(), cpu.bus.readUByte(0x01u))
    }

    @Test fun test_stx_zero_page() {
        val program = ubyteArrayOf(0xa2u, 0x02u, 0x86u, 0x01u, 0x00u)
        loadAndRun(program)
        assertEquals(0x02.toUByte(), cpu.bus.readUByte(0x01u))
    }

    @Test fun test_sty_zero_page() {
        val program = ubyteArrayOf(0xa0u, 0x02u, 0x84u, 0x01u, 0x00u)
        loadAndRun(program)
        assertEquals(0x02.toUByte(), cpu.bus.readUByte(0x01u))
    }

    @Test fun test_tax() {
        val program = ubyteArrayOf(0xa9u, 0x06u, 0xaau, 0x00u)
        loadAndRun(program)
        assertEquals(6.toUByte(), cpu.a)
        assertEquals(6.toUByte(), cpu.x)
    }

    @Test fun test_tya() {
        val program = ubyteArrayOf(0xa0u, 0x06u, 0x98u, 0x00u)
        loadAndRun(program)
        assertEquals(6.toUByte(), cpu.y)
        assertEquals(6.toUByte(), cpu.a)
    }

    @Test fun test_tya_zero() {
        val program = ubyteArrayOf(0xa0u, 0x00u, 0x98u, 0x00u)
        loadAndRun(program)
        assertEquals(0.toUByte(), cpu.y)
        assertEquals(0.toUByte(), cpu.a)
        assertTrue(cpu.status.getStatus(Flag.ZERO))
    }

    @Test fun test_tya_neg() {
        val program = ubyteArrayOf(0xa0u, 0xffu, 0x98u, 0x00u)
        loadAndRun(program)
        assertEquals(0xff.toUByte(), cpu.y)
        assertEquals(0xff.toUByte(), cpu.a)
        assertTrue(cpu.status.getStatus(Flag.NEGATIVE))
    }

    @Test fun test_pha_pla() {
        val program = ubyteArrayOf(0xa9u, 0xffu, 0x48u, 0xa9u, 0x00u, 0x68u, 0x00u)
        loadAndRun(program)
        assertEquals(0xff.toUByte(), cpu.a)
        assertTrue(cpu.status.getStatus(Flag.NEGATIVE))
    }

    @Test fun test_and() {
        cpu.bus.writeUByte(0x00u, 0x01u)
        val program = ubyteArrayOf(0xa9u, 0xffu, 0x25u, 0x00u, 0x00u)
        loadAndRun(program)
        assertEquals(1.toUByte(), cpu.a)
        assertFalse(cpu.status.getStatus(Flag.NEGATIVE))
        assertFalse(cpu.status.getStatus(Flag.ZERO))
    }

    @Test fun test_ora() {
        cpu.bus.writeUByte(0x00u, 0x01u)
        val program = ubyteArrayOf(0xa9u, 0xf0u, 0x05u, 0x00u, 0x00u)
        loadAndRun(program)
        assertEquals(0xf1.toUByte(), cpu.a)
        assertTrue(cpu.status.getStatus(Flag.NEGATIVE))
        assertFalse(cpu.status.getStatus(Flag.ZERO))
    }

    @Test fun test_eor() {
        cpu.bus.writeUByte(0x00u, 0x01u)
        val program = ubyteArrayOf(0xa9u, 0xf0u, 0x45u, 0x00u, 0x00u)
        loadAndRun(program)
        assertEquals(0xf1.toUByte(), cpu.a)
        assertTrue(cpu.status.getStatus(Flag.NEGATIVE))
        assertFalse(cpu.status.getStatus(Flag.ZERO))
    }

    @Test fun test_inx_iny() {
        val program = ubyteArrayOf(0xa9u, 0xc0u, 0xaau, 0xe8u, 0x00u)
        loadAndRun(program)
        assertEquals(0xc1.toUByte(), cpu.x)
    }

    @Test fun test_inx_overflow() {
        val program = ubyteArrayOf(0xa2u, 0xffu, 0xe8u, 0xe8u, 0x00u)
        loadAndRun(program)
        assertEquals(1.toUByte(), cpu.x)
    }

    @Test fun test_adc_overflow() {
        val program = ubyteArrayOf(0xa9u, 0x7fu, 0x69u, 0x7fu, 0x00u)
        loadAndRun(program)
        assertEquals(0b1100_0100u.toUByte(), cpu.status.get())
    }

    @Test fun test_sbc_carry() {
        val program = ubyteArrayOf(0x38u, 0xa9u, 0x40u, 0xe9u, 0x40u, 0x00u)
        loadAndRun(program)
        assertEquals(0b0000_0111.toUByte(), cpu.status.get())
    }

    @Test fun test_sbc_overflow() {
        val program = ubyteArrayOf(0x38u, 0xa9u, 0x40u, 0xe9u, 0x41u, 0x00u)
        loadAndRun(program)
        assertEquals(0b1000_0100u.toUByte(), cpu.status.get())
    }

    @Test fun test_sbc_carry_overflow() {
        val program = ubyteArrayOf(0xa9u, 0x80u, 0xe9u, 0x00u, 0x00u)
        loadAndRun(program, os = System.out)
        assertEquals(0b0100_0101u.toUByte(), cpu.status.get())
    }

    @Test fun test_klaus2m5() {
        val firmware = java.io.File("firmware/6502_F~1.BIN")
        val program = firmware.readBytes().toUByteArray()

        // http://forum.6502.org/viewtopic.php?f=8&t=5298
        cpu.bus.populate(program, 0x0000)
        cpu.a = 0u
        cpu.x = 0u
        cpu.y = 0u
        cpu.sp = 0xffu
        cpu.pc = 0x0400u
        cpu.status(0u)
        cpu.run(maxStep = 1000000)

        // This will ALWAYS fail
        // Check `cpu.pc` to see if it's stuck in some trap, i.e. infinite loop
        assertEquals("?", "%04x".format(cpu.pc.toInt()))
    }

    @Test fun test_rom() {
        val testRom = java.io.File("src/main/assets/testRom/nestest.nes")
        val fileOutputStream = PrintStream(java.io.File("src/main/assets/testRom/nestest.out"))
        val program = testRom.readBytes().toUByteArray()
        cpu.switchBus(NESBus)
        cpu.bus.populate(program, 0xc000)
        cpu.a = 0u
        cpu.x = 0u
        cpu.y = 0u
        cpu.status(0x24u)
        cpu.sp = 0xfdu
        cpu.pc = 0xc000u
        try {
            CPU.run(os = System.out)
        } catch (e: NESException) {
            println(e.msg)
            throw e
        } finally {
            fileOutputStream.close()
        }
    }

}