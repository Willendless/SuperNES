package com.willendless.nes.emulator.cpu

import com.willendless.nes.emulator.cpu.AddressMode.*

@ExperimentalUnsignedTypes
object OpcodeMap {
    @ExperimentalUnsignedTypes
    data class Opcode(val code: UByte = 0u, val name: String = "Unknown", val len: UShort = 0u, val mode: AddressMode = NoneAddressing)

    private val opcodesList = arrayListOf<Opcode>(
            Opcode(0xe8u, "INX", 1u, NoneAddressing),
            // LDA
            Opcode(0xa9u, "LDA", 2u, Immediate),
            Opcode(0xa5u, "LDA", 2u, ZeroPage),
            Opcode(0xb5u, "LDA", 2u, ZeroPageX),
            Opcode(0xadu, "LDA", 3u, Absolute),
            Opcode(0xbdu, "LDA", 3u, AbsoluteX),
            Opcode(0xb9u, "LDA", 3u, AbsoluteY),
            Opcode(0xa1u, "LDA", 2u, IndirectX),
            Opcode(0xb1u, "LDA", 2u, IndirectY),
            // LDX
            Opcode(0xa2u, "LDX", 2u, Immediate),
            Opcode(0xa6u, "LDX", 2u, ZeroPage),
            Opcode(0xb6u, "LDX", 2u, ZeroPageY),
            Opcode(0xaeu, "LDX", 3u, Absolute),
            Opcode(0xb3u, "LDX", 3u, AbsoluteY),
            // LDY
            Opcode(0xA0u, "LDY", 2u, Immediate),
            Opcode(0xA4u, "LDY", 2u, ZeroPage),
            Opcode(0xB4u, "LDY", 2u, ZeroPageX),
            Opcode(0xAcu, "LDY", 3u, Absolute),
            Opcode(0xBCu, "LDY", 3u, AbsoluteX),
            // STA
            Opcode(0x85u, "STA", 2u, ZeroPage),
            Opcode(0x95u, "STA", 2u, ZeroPageX),
            Opcode(0x8Du, "STA", 3u, Absolute),
            Opcode(0x9Du, "STA", 3u, AbsoluteX),
            Opcode(0x99u, "STA", 3u, AbsoluteY),
            Opcode(0x81u, "STA", 2u, IndirectX),
            Opcode(0x91u, "STA", 2u, IndirectY),
            // STX
            Opcode(0x86u, "STX", 2u, ZeroPage),
            Opcode(0x96u, "STX", 2u, ZeroPageY),
            Opcode(0x8Eu, "STX", 3u, Absolute),
            // STY
            Opcode(0x84u, "STY", 2u, ZeroPage),
            Opcode(0x94u, "STY", 2u, ZeroPageX),
            Opcode(0x8Cu, "STY", 3u, Absolute),
            // Register Transfer
            Opcode(0xaau, "TAX", 1u, NoneAddressing),
            Opcode(0xA8u, "TAY", 1u, NoneAddressing),
            Opcode(0xBAu, "TSX", 1u, NoneAddressing),
            Opcode(0x8Au, "TXA", 1u, NoneAddressing),
            Opcode(0x9Au, "TXS", 1u, NoneAddressing),
            Opcode(0x98u, "TYA", 1u, NoneAddressing),
            // Stack push & pop
            Opcode(0x48u, "PHA", 1u, NoneAddressing),
            Opcode(0x08u, "PHP", 1u, NoneAddressing),
            Opcode(0x68u, "PLA", 1u, NoneAddressing),
            Opcode(0x28u, "PLP", 1u, NoneAddressing),
            // Logical Operations
            Opcode(0x29u, "AND", 2u, Immediate),
            Opcode(0x25u, "AND", 2u, ZeroPage),
            Opcode(0x35u, "AND", 2u, ZeroPageX),
            Opcode(0x2Du, "AND", 3u, Absolute),
            Opcode(0x3Du, "AND", 3u, AbsoluteX),
            Opcode(0x39u, "AND", 3u, AbsoluteY),
            Opcode(0x21u, "AND", 2u, IndirectX),
            Opcode(0x31u, "AND", 2u, IndirectY),
            Opcode(0x09u, "ORA", 2u, Immediate),
            Opcode(0x05u, "ORA", 2u, ZeroPage),
            Opcode(0x15u, "ORA", 2u, ZeroPageX),
            Opcode(0x0Du, "ORA", 3u, Absolute),
            Opcode(0x1Du, "ORA", 3u, AbsoluteX),
            Opcode(0x19u, "ORA", 3u, AbsoluteY),
            Opcode(0x01u, "ORA", 2u, IndirectX),
            Opcode(0x11u, "ORA", 2u, IndirectY),
            Opcode(0x49u, "EOR", 2u, Immediate),
            Opcode(0x45u, "EOR", 2u, ZeroPage),
            Opcode(0x55u, "EOR", 2u, ZeroPageX),
            Opcode(0x4Du, "EOR", 3u, Absolute),
            Opcode(0x5Du, "EOR", 3u, AbsoluteX),
            Opcode(0x59u, "EOR", 3u, AbsoluteY),
            Opcode(0x41u, "EOR", 2u, IndirectX),
            Opcode(0x51u, "EOR", 2u, IndirectY),
            Opcode(0x24u, "BIT", 2u, ZeroPage),
            Opcode(0x2Cu, "BIT", 3u, Absolute),
            Opcode(0xC9u, "CMP", 2u, Immediate),
            Opcode(0xC5u, "CMP", 2u, ZeroPage),
            Opcode(0xD5u, "CMP", 2u, ZeroPageX),
            Opcode(0xCDu, "CMP", 3u, Absolute),
            Opcode(0xDDu, "CMP", 3u, AbsoluteX),
            Opcode(0xD9u, "CMP", 3u, AbsoluteY),
            Opcode(0xC1u, "CMP", 2u, IndirectX),
            Opcode(0xD1u, "CMP", 2u, IndirectY),
            Opcode(0xE0u, "CPX", 2u, Immediate),
            Opcode(0xE4u, "CPX", 2u, ZeroPage),
            Opcode(0xECu, "CPX", 3u, Absolute),
            Opcode(0xC0u, "CPY", 2u, Immediate),
            Opcode(0xC4u, "CPY", 3u, ZeroPage),
            Opcode(0xCCu, "CPY", 3u, Absolute),
            // Arithmetic Operations
            Opcode(0x69u, "ADC", 2u, Immediate),
            Opcode(0x65u, "ADC", 2u, ZeroPage),
            Opcode(0x75u, "ADC", 2u, ZeroPageX),
            Opcode(0x6Du, "ADC", 3u, Absolute),
            Opcode(0x7Du, "ADC", 3u, AbsoluteX),
            Opcode(0x79u, "ADC", 3u, AbsoluteY),
            Opcode(0x61u, "ADC", 2u, IndirectX),
            Opcode(0x71u, "ADC", 2u, IndirectY),
            Opcode(0xE9u, "SBC", 2u, Immediate),
            Opcode(0xE5u, "SBC", 2u, ZeroPage),
            Opcode(0xF5u, "SBC", 2u, ZeroPageX),
            Opcode(0xEDu, "SBC", 3u, Absolute),
            Opcode(0xFDu, "SBC", 3u, AbsoluteX),
            Opcode(0xF9u, "SBC", 3u, AbsoluteY),
            Opcode(0xE1u, "SBC", 2u, IndirectX),
            Opcode(0xF1u, "SBC", 2u, IndirectY),
            // Inc & Dec
            Opcode(0xE6u, "INC", 2u, ZeroPage),
            Opcode(0xF5u, "INC", 2u, ZeroPageX),
            Opcode(0xEEu, "INC", 3u, Absolute),
            Opcode(0xFEu, "INC", 3u, AbsoluteY),
            Opcode(0xE8u, "INX", 1u, NoneAddressing),
            Opcode(0xC8u, "INY", 1u, NoneAddressing),
            Opcode(0xC6u, "DEC", 2u, ZeroPage),
            Opcode(0xD6u, "DEC", 2u, ZeroPageX),
            Opcode(0xCEu, "DEC", 3u, Absolute),
            Opcode(0xDEu, "DEC", 3u, AbsoluteX),
            Opcode(0xCAu, "DEX", 1u, NoneAddressing),
            Opcode(0x88u, "DEY", 1u, NoneAddressing),
            // Shifts
            Opcode(0x0Au, "ASL", 1u, NoneAddressing),
            Opcode(0x06u, "ASL", 2u, ZeroPage),
            Opcode(0x16u, "ASL", 2u, ZeroPageX),
            Opcode(0x0Eu, "ASL", 3u, Absolute),
            Opcode(0x1Eu, "ASL", 3u, AbsoluteX),
            Opcode(0x4Au, "LSR", 1u, NoneAddressing),
            Opcode(0x46u, "LSR", 2u, ZeroPage),
            Opcode(0x56u, "LSR", 2u, ZeroPageX),
            Opcode(0x4Eu, "LSR", 3u, Absolute),
            Opcode(0x5Eu, "LSR", 3u, AbsoluteX),
            Opcode(0x2Au, "ROL", 1u, NoneAddressing),
            Opcode(0x26u, "ROL", 2u, ZeroPage),
            Opcode(0x36u, "ROL", 2u, ZeroPageX),
            Opcode(0x2Eu, "ROL", 3u, Absolute),
            Opcode(0x3Eu, "ROL", 3u, AbsoluteX),
            Opcode(0x6Au, "ROR", 1u, NoneAddressing),
            Opcode(0x66u, "ROR", 2u, ZeroPage),
            Opcode(0x76u, "ROR", 2u, ZeroPageX),
            Opcode(0x6Eu, "ROR", 3u, Absolute),
            Opcode(0x7Eu, "ROR", 3u, AbsoluteX),
            // Jumps & Calls
            Opcode(0x4Cu, "JMP", 3u, Absolute),
            Opcode(0x6Cu, "JMP", 3u, Indirect),
            Opcode(0x20u, "JSR", 3u, Absolute),
            Opcode(0x60u, "RTS", 1u, NoneAddressing),
            // Branches
            Opcode(0x90u, "BCC", 2u, NoneAddressing),
            Opcode(0xB0u, "BCS", 2u, NoneAddressing),
            Opcode(0xF0u, "BEQ", 2u, NoneAddressing),
            Opcode(0x30u, "BMI", 2u, NoneAddressing),
            Opcode(0xD0u, "BNE", 2u, NoneAddressing),
            Opcode(0x10u, "BPL", 2u, NoneAddressing),
            Opcode(0x50u, "BVC", 2u, NoneAddressing),
            Opcode(0x70u, "BVS", 2u, NoneAddressing),
            // Status Flag Changes
            Opcode(0x18u, "CLC", 1u, NoneAddressing),
            Opcode(0xD8u, "CLD", 1u, NoneAddressing),
            Opcode(0x58u, "CLI", 1u, NoneAddressing),
            Opcode(0xB8u, "CLV", 1u, NoneAddressing),
            Opcode(0x38u, "SEC", 1u, NoneAddressing),
            Opcode(0xF8u, "SED", 1u, NoneAddressing),
            Opcode(0x78u, "SEI", 1u, NoneAddressing),
            // System Functions
            Opcode(0x00u, "BRK", 1u, NoneAddressing),
            Opcode(0xEAu, "NOP", 1u, NoneAddressing),
            Opcode(0x40u, "RTI", 1u, NoneAddressing)
    )

    private val opcodes = HashMap<UByte, Opcode>().apply {
        for (opcode in opcodesList) {
            this[opcode.code] = opcode
        }
    }

    fun getOpcode(code: UByte) = opcodes.getOrElse(code) { Opcode() }
}