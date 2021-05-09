package com.willendless.nes.emulator


@ExperimentalUnsignedTypes
object CPU {
    var a = Reg8bits()
    var x = Reg8bits()
    var y = Reg8bits()
    var pc = PC()
    var sp = Reg8bits()
    var status = PStatus
    var memory = Mem(0x10000)
    private val opcodeMap = OpcodeMap

    fun loadAndRun(program: UByteArray) {
        // insert cartridge
        program.copyInto(memory.mem, 0x8000, 0, program.size)
        // reset vector
        memory.writeUnsignedShort(0xFFFC, 0x8000)
        // reset
        reset()
        run()
    }

    private fun reset() {
        a.set(0)
        x.set(0)
        y.set(0)
        status.set(0)
        sp.set(0xfd) // See https://github.com/bugzmanov/nes_ebook/issues/10
        pc.set(memory.readUnsignedShort(0xFFFC))
    }

    // Get current instruction byte without range check
    // and increment pc.
    // SIDE EFFECT: the value of pc will be incremented by 1.
    private fun fetchCode() = with(memory) {
        pc++
        readUnsignedByte(pc.getValUnsigned() - 1)
    }

    // Get current instruction byte without range check.
    // No side effect.
    private fun peekCode() = memory.readUnsignedByte(pc.getValUnsigned())

    // Return operand according to addressing mode.
    // No side effect to pc or any other registers.
    // REQUIRES: before called, pc should point to the second byte of the instruction.
    // ENSURES: return the memory address of the operand
    private fun getOperandAddress(mode: AddressMode): Int {
        return when (mode) {
            AddressMode.Immediate -> pc.getValUnsigned()
            AddressMode.ZeroPage -> peekCode()
            AddressMode.ZeroPageX -> x + peekCode()
            AddressMode.ZeroPageY -> y + peekCode()
            AddressMode.Absolute -> memory.readUnsignedShort(pc.getValUnsigned())
            AddressMode.AbsoluteX -> memory.readUnsignedShort(pc.getValUnsigned()) + x.getValSigned()
            AddressMode.AbsoluteY -> memory.readUnsignedShort(pc.getValUnsigned()) + y.getValSigned()
            AddressMode.Indirect -> memory.readUnsignedShort(memory.readUnsignedShort(pc.getValUnsigned()))
            AddressMode.IndirectX -> memory.readUnsignedShort(x + peekCode())
            AddressMode.IndirectY -> memory.readUnsignedShort(peekCode()) + y.getValSigned()
            AddressMode.NoneAddressing -> unreachable("$mode not supported")
        }
    }

    private fun lda(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        val value = memory.readUnsignedByte(addr)
        a.set(value)
        status.updateNZ(a)
    }

    private fun ldx(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        val value = memory.readUnsignedByte(addr)
        x.set(value)
        status.updateNZ(x)
    }

    private fun ldy(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        val value = memory.readUnsignedByte(addr)
        y.set(value)
        status.updateNZ(y)
    }

    private fun sta(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        memory.writeUnsignedByte(addr, a.getValUnsigned())
    }

    private fun stx(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        memory.writeUnsignedByte(addr, x.getValUnsigned())
    }

    private fun sty(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        memory.writeUnsignedByte(addr, y.getValUnsigned())
    }

    private fun tax() {
        x = a.copy()
        status.updateNZ(x)
    }

    private fun tay() {
        y = a.copy()
        status.updateNZ(y)
    }

    private fun tsx() {
        x = sp.copy()
        status.updateNZ(x)
    }

    private fun txa() {
        a = sp.copy()
        status.updateNZ(a)
    }

    private fun txs() {
        sp = x.copy()
        status.updateNZ(sp)
    }

    private fun tya() {
        a = y.copy()
        status.updateNZ(a)
    }

    private fun pha() {
        memory.writeUnsignedByte(sp + 0x100, a.getValUnsigned())
        sp--
    }

    private fun php() {
        val value = status.getValUnsigned() or 0b0011_0000
        memory.writeUnsignedByte(sp + 0x100, value)
        sp--
        // no side effect
    }

    private fun pla() {
        sp++
        a.set(memory.readUnsignedByte(sp + 0x100))
        status.updateNZ(a)
    }

    private fun plp() {
        sp++
        val value = memory.readSignedByte(sp + 0x100) and 0b1100_1111
        status.set(value)
    }

    private fun and(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        a.set(a.getValUnsigned() and memory.readUnsignedByte(addr))
        status.updateNZ(a)
    }

    private fun ora(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        a.set(a.getValUnsigned() or memory.readUnsignedByte(addr))
        status.updateNZ(a)
    }

    private fun eor(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        a.set(a.getValUnsigned() xor memory.readUnsignedByte(addr))
        status.updateNZ(a)
    }

    private fun bit(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        val value = memory.readUnsignedByte(addr)
        if (value and 0b0100_0000 > 0) status.setV()
        status.updateN(value)
        val res = value and a.getValUnsigned()
        status.updateZ(res)
    }

    private fun adc(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        // unsigned
        val uvalue = memory.readUnsignedByte(addr)
        val ures = uvalue + a.getValUnsigned() + status.c()
        status.updateC(ures)
        // signed
        val value = memory.readSignedByte(addr)
        val res = value + a.getValSigned() + status.c()
        status.updateV(res)
        // wrap around update a
        a.set(ures)
        status.updateNZ(a)
    }

    private fun sbc(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        // unsigned
        val uvalue = memory.readUnsignedByte(addr)
        val ures = a.getValUnsigned() - uvalue - (1 - status.c())
        status.updateC(ures)
        // signed
        val value = memory.readSignedByte(addr)
        val res = a.getValSigned() - value - (1 - status.c())
        status.updateV(res)
        // wrap around update a
        a.set(ures)
        status.updateNZ(a)
    }

    private fun cmp(addressMode: AddressMode, reg: Reg8bits) {
        val addr = getOperandAddress(addressMode)
        val value = memory.readUnsignedByte(addr)
        val res = reg.getValUnsigned() - value
        status.updateC(res)
        status.updateN(res)
        status.updateZ(res)
    }

    private fun inc(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        val res = memory.readUnsignedByte(addr) + 1
        memory.writeUnsignedByte(addr, res)
        status.updateNZ(res)
    }

    private fun inx() {
        x++
        status.updateNZ(x)
    }

    private fun iny() {
        y++
        status.updateNZ(y)
    }

    private fun dec(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        val res = memory.readUnsignedByte(addr) - 1
        memory.writeUnsignedByte(addr, res)
        status.updateNZ(res)
    }

    private fun dex() {
        x--
        status.updateNZ(x)
    }

    private fun dey() {
        y--
        status.updateNZ(y)
    }

    private fun asl() {
        val value = a.getValUnsigned()
        if (value and 0b1000_0000 > 0) status.setC()
        a.set(value shl 1)
        status.updateNZ(a)
    }

    private fun asl(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        val value = memory.readUnsignedByte(addr)
        if (value and 0b1000_0000 > 0) status.setC()
        memory.writeUnsignedByte(addr, value shl 1)
        status.updateNZ(memory.readUnsignedByte(addr))
    }

    private fun lsr() {
        val value = a.getValUnsigned()
        if (value and 0b0000_0001 > 0) status.setC()
        a.set(value shr 1)
        status.updateNZ(a)
    }

    private fun lsr(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        val value = memory.readUnsignedByte(addr)
        if (value and 0b0000_0001 > 0) status.setC()
        memory.writeUnsignedByte(addr, value shr 1)
        status.updateNZ(memory.readUnsignedByte(addr))
    }

    private fun rol() {
        val value = a.getValUnsigned()
        if (value and 0b1000_0000 > 0) status.setC()
        a.set(value shl 1 and status.c())
        status.updateNZ(a)
    }

    private fun rol(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        val value = memory.readUnsignedByte(addr)
        if (value and 0b1000_0000 > 0) status.setC()
        memory.writeUnsignedByte(addr, value shl 1 and status.c())
        status.updateNZ(memory.readUnsignedByte(addr))
    }

    private fun ror() {
        val value = a.getValUnsigned()
        if (value and 0b0000_0001 > 0) status.setC()
        a.set(value shr 1 or (status.c() shl 7))
        status.updateNZ(a)
    }

    private fun ror(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        val value = memory.readUnsignedByte(addr)
        if (value and 0b0000_0001 > 0) status.setC()
        memory.writeUnsignedByte(addr, value shr 1 or (status.c() shl 7))
        status.updateNZ(memory.readUnsignedByte(addr))
    }

    private fun jmp(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        val value = memory.readUnsignedShort(addr)
        pc.set(value)
    }

    private fun jsr(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        val jumpAddr = memory.readUnsignedShort(addr)
        val retAddr = pc.getValUnsigned() + 2 - 1
        memory.writeUnsignedShort(sp + 0x100, retAddr)
        sp--
        sp--
        pc.set(jumpAddr)
    }

    private fun rts() {
        val retAddr = memory.readUnsignedShort(sp.getValUnsigned()) + 1
        pc.set(retAddr)
    }

    private fun brk() {
        memory.writeUnsignedShort(sp.getValUnsigned(), pc.getValUnsigned())
        sp++
        memory.writeUnsignedByte(sp.getValUnsigned() or 0b0011_0000, status.getValUnsigned())
        sp++
        pc.set(memory.readUnsignedShort(0xFFFE))
        // side effect of brk is set I to 1
        status.setI()
    }

    private fun rti() {
        sp--
        status.set(memory.readUnsignedByte(sp.getValUnsigned()))
        sp--
        pc.set(memory.readUnsignedShort(sp.getValUnsigned()))
    }

    // Run game in the memory begin from 0x8000.
    private fun run() {
        while (true) {
            val code = fetchCode()
            val opcode = opcodeMap.getOpcode(code)
            println("$code: $opcode")
            when (opcode.code) {
                // load/store
                0xA9, 0xA5, 0xB5, 0xAD, 0xBD, 0xB9, 0xA1, 0xB1 -> lda(opcode.mode)
                0xA2, 0xA6, 0xB6, 0xAE, 0xBE -> ldx(opcode.mode)
                0xA0, 0xA4, 0xB4, 0xAC, 0xBC -> ldy(opcode.mode)
                0x85, 0x95, 0x8D, 0x9D, 0x99, 0x81, 0x91 -> sta(opcode.mode)
                0x86, 0x96, 0x8E -> stx(opcode.mode)
                0x84, 0x94, 0x8C -> sty(opcode.mode)
                // transfer
                0xAA -> tax() // x = a
                0xA8 -> tay()
                0xBA -> tsx()
                0x8A -> txa()
                0x9A -> txs()
                0x98 -> tya()
                // stack
                0x48 -> pha()
                0x08 -> php()
                0x68 -> pla()
                0x28 -> plp()
                // logic
                0x29, 0x25, 0x35, 0x2D, 0x3D, 0x39, 0x21, 0x31 -> and(opcode.mode)
                0x09, 0x05, 0x15, 0x0D, 0x1D, 0x19, 0x01, 0x11 -> ora(opcode.mode)
                0x49, 0x45, 0x55, 0x4D, 0x5D, 0x59, 0x41, 0x51 -> eor(opcode.mode)
                0x24, 0x2C -> bit(opcode.mode)
                // arithmetic
                0x69, 0x65, 0x75, 0x6D, 0x7D, 0x79, 0x61, 0x71 -> adc(opcode.mode)
                0xE9, 0xE5, 0xF5, 0xED, 0xFD, 0xF9, 0xE1, 0xF1 -> sbc(opcode.mode)
                0xC9, 0xC5, 0xD5, 0xCD, 0xDD, 0xD9, 0xC1, 0xD1 -> cmp(opcode.mode, a)
                0xE0, 0xE4, 0xEc -> cmp(opcode.mode, x)
                0xC0, 0xC4, 0xCC -> cmp(opcode.mode, y)
                // inc & dec
                0xE6, 0xF6, 0xEE, 0xFE -> inc(opcode.mode)
                0xE8 -> inx() // x++
                0xC8 -> iny() // y++
                0xC6, 0xD6, 0xCe, 0xDE -> dec(opcode.mode)
                0xCA -> dex()
                0x88 -> dey()
                // shift
                0x0A -> asl()
                0x06, 0x16, 0x0E, 0x1E -> asl(opcode.mode)
                0x4A -> lsr()
                0x46, 0x56, 0x4E, 0x5E -> lsr(opcode.mode)
                0x2A -> rol()
                0x26, 0x36, 0x2E, 0x3E -> rol(opcode.mode)
                0x6A -> ror()
                0x66, 0x76, 0x6E, 0x7E -> ror(opcode.mode)
                // jump & calls
                0x4C, 0x6C -> jmp(opcode.mode)
                0x20 -> jsr(opcode.mode)
                0x60 -> rts()
                // branches
                0x90 -> if (status.c() == 0) pc.set(pc.getValUnsigned() + peekCode())
                0xB0 -> if (status.c() == 1) pc.set(pc.getValUnsigned() + peekCode())
                0xF0 -> if (status.z() == 1) pc.set(pc.getValUnsigned() + peekCode())
                0x30 -> if (status.n() == 1) pc.set(pc.getValUnsigned() + peekCode())
                0xD0 -> if (status.z() == 0) pc.set(pc.getValUnsigned() + peekCode())
                0x10 -> if (status.n() == 0) pc.set(pc.getValUnsigned() + peekCode())
                0x50 -> if (status.v() == 0) pc.set(pc.getValUnsigned() + peekCode())
                0x70 -> if (status.v() == 1) pc.set(pc.getValUnsigned() + peekCode())
                // status
                0x18 -> status.clearC()
                0xD8 -> status.clearD()
                0x58 -> status.clearI()
                0xB8 -> status.clearV()
                0x38 -> status.setC()
                0xF8 -> status.setD()
                0x78 -> status.setI()
                // system functions: nop, brk, rti
                0xEA -> {
                } // nop
                0x40 -> rti()
                0x00 -> {
                    brk()
                    return
                }
                else -> unreachable("opcode is $opcode")
            }
            pc.set(pc.getValUnsigned() + opcode.len - 1)
        }
    }
}
