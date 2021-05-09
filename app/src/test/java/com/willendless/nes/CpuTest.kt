package com.willendless.nes

import com.willendless.nes.emulator.CPU
import com.willendless.nes.emulator.Reg8bits
import junit.framework.Assert.assertEquals
import org.junit.Test

@ExperimentalUnsignedTypes
class CPUTest {
    private val cpu = CPU

    @Test fun test_lda_immediate_load_data() {
        val program = ubyteArrayOf(0xa9u, 0x05u, 0x00u)
        cpu.loadAndRun(program)
        assertEquals(Reg8bits(0x05), cpu.a)
        assertEquals(0, cpu.status.z())
        assertEquals(0, cpu.status.n())
    }
    
    @Test fun test_lda_zero_flag() {
        val program = ubyteArrayOf(0xa9u, 0x00u, 0x00u)
        cpu.loadAndRun(program)
        assertEquals(1, cpu.status.z())
    }

    @Test fun test_lda_zero_page() {
        cpu.memory.writeUnsignedByte(0x00, 0x01)
        val program = ubyteArrayOf(0xa5u, 0x00u, 0x00u)
        cpu.loadAndRun(program)
        assertEquals(Reg8bits(1), cpu.a)
    }

    @Test fun test_lda_zero_page_x() {
        cpu.memory.writeUnsignedByte(0x01, 0x01)
        val program = ubyteArrayOf(0xa2u, 0x01u, 0xb5u, 0x00u, 0x00u)
        cpu.loadAndRun(program)
        assertEquals(Reg8bits(1), cpu.a)
    }

    @Test fun test_lda_zero_page_x_overflow() {
        cpu.memory.writeUnsignedByte(0x00, 0x02)
        val program = ubyteArrayOf(0xa2u, 0xffu, 0xb5u, 0x01u, 0x00u)
        cpu.loadAndRun(program)
        assertEquals(Reg8bits(2), cpu.a)
    }

    @Test fun test_lda_absolute() {
        cpu.memory.writeUnsignedShort(0x00, 0x08)
        val program = ubyteArrayOf(0xadu, 0x00u, 0x00u, 0x00u)
        cpu.loadAndRun(program)
        assertEquals(Reg8bits(8), cpu.a)
    }

    @Test fun test_lda_absolute_x() {
        cpu.memory.writeUnsignedShort(0x10, 0x09)
        val program = ubyteArrayOf(0xa2u, 0x10u, 0xbdu, 0x00u, 0x00u, 0x00u)
        cpu.loadAndRun(program)
        assertEquals(Reg8bits(9), cpu.a)
    }

    @Test fun test_lda_absolute_y() {
        cpu.memory.writeUnsignedShort(0x10, 0x09)
        val program = ubyteArrayOf(0xa0u, 0x10u, 0xb9u, 0x00u, 0x00u, 0x00u)
        cpu.loadAndRun(program)
        assertEquals(Reg8bits(9), cpu.a)
    }

    @Test fun test_lda_indirect_x() {
        cpu.memory.writeUnsignedShort(0x00, 0x10)
        cpu.memory.writeUnsignedShort(0x10, 0x10)
        val program = ubyteArrayOf(0xa2u, 0x01u, 0xa1u, 0xffu, 0x00u)
        cpu.loadAndRun(program)
        assertEquals(Reg8bits(0x10), cpu.a)
    }

    @Test fun test_lda_indirect_y() {
        cpu.memory.writeUnsignedShort(0xbb, 0x1122)
        cpu.memory.writeUnsignedShort(0x1124, 0xaa)
        val program = ubyteArrayOf(0xa0u, 0x02u, 0xb1u, 0xbbu, 0x00u)
        cpu.loadAndRun(program)
        assertEquals(Reg8bits(0xaa), cpu.a)
    }

    @Test fun test_sta_zero_page() {
        val program = ubyteArrayOf(0xa9u, 0x02u, 0x85u, 0x01u, 0x00u)
        cpu.loadAndRun(program)
        assertEquals(0x02, cpu.memory.readUnsignedByte(0x01))
    }

    @Test fun test_stx_zero_page() {
        val program = ubyteArrayOf(0xa2u, 0x02u, 0x86u, 0x01u, 0x00u)
        cpu.loadAndRun(program)
        assertEquals(0x02, cpu.memory.readUnsignedByte(0x01))
    }

    @Test fun test_sty_zero_page() {
        val program = ubyteArrayOf(0xa0u, 0x02u, 0x84u, 0x01u, 0x00u)
        cpu.loadAndRun(program)
        assertEquals(0x02, cpu.memory.readUnsignedByte(0x01))
    }

    @Test fun test_tax() {
        val program = ubyteArrayOf(0xa9u, 0x06u, 0xaau, 0x00u)
        cpu.loadAndRun(program)
        assertEquals(Reg8bits(6), cpu.a)
        assertEquals(Reg8bits(6), cpu.x)
    }

    @Test fun test_tya() {
        val program = ubyteArrayOf(0xa0u, 0x06u, 0x98u, 0x00u)
        cpu.loadAndRun(program)
        assertEquals(Reg8bits(6), cpu.y)
        assertEquals(Reg8bits(6), cpu.a)
    }

    @Test fun test_tya_zero() {
        val program = ubyteArrayOf(0xa0u, 0x00u, 0x98u, 0x00u)
        cpu.loadAndRun(program)
        assertEquals(Reg8bits(0), cpu.y)
        assertEquals(Reg8bits(0), cpu.a)
        assertEquals(1, cpu.status.z())
    }

    @Test fun test_tya_neg() {
        val program = ubyteArrayOf(0xa0u, 0xffu, 0x98u, 0x00u)
        cpu.loadAndRun(program)
        assertEquals(Reg8bits(0xff), cpu.y)
        assertEquals(Reg8bits(0xff), cpu.a)
        assertEquals(1, cpu.status.n())
    }

    @Test fun test_pha_pla() {
        val program = ubyteArrayOf(0xa9u, 0xffu, 0x48u, 0xa9u, 0x00u, 0x68u, 0x00u)
        cpu.loadAndRun(program)
        assertEquals(Reg8bits(0xff), cpu.a)
        assertEquals(1, cpu.status.n())
    }

    @Test fun test_and() {
        cpu.memory.writeUnsignedByte(0x00, 0x01)
        val program = ubyteArrayOf(0xa9u, 0xffu, 0x25u, 0x00u, 0x00u)
        cpu.loadAndRun(program)
        assertEquals(Reg8bits(1), cpu.a)
        assertEquals(0, cpu.status.n())
        assertEquals(0, cpu.status.z())
    }

    @Test fun test_ora() {
        cpu.memory.writeUnsignedByte(0x00, 0x01)
        val program = ubyteArrayOf(0xa9u, 0xf0u, 0x05u, 0x00u, 0x00u)
        cpu.loadAndRun(program)
        assertEquals(Reg8bits(0xf1), cpu.a)
        assertEquals(1, cpu.status.n())
        assertEquals(0, cpu.status.z())
    }

    @Test fun test_eor() {
        cpu.memory.writeUnsignedByte(0x00, 0x01)
        val program = ubyteArrayOf(0xa9u, 0xf0u, 0x45u, 0x00u, 0x00u)
        cpu.loadAndRun(program)
        assertEquals(Reg8bits(0xf1), cpu.a)
        assertEquals(1, cpu.status.n())
        assertEquals(0, cpu.status.z())
    }

    @Test fun test_inx_iny() {
        val program = ubyteArrayOf(0xa9u, 0xc0u, 0xaau, 0xe8u, 0x00u)
        cpu.loadAndRun(program)
        assertEquals(Reg8bits(0xc1), cpu.x)
    }

    @Test fun test_inx_overflow() {
        val program = ubyteArrayOf(0xa2u, 0xffu, 0xe8u, 0xe8u, 0x00u)
        cpu.loadAndRun(program)
        assertEquals(Reg8bits(1), cpu.x)
    }

    @Test fun test_adc() {

    }

    @Test fun test_sbc() {

    }
}