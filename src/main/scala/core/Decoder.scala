package core

import core.OpCodes._
import spinal.core._
import spinal.lib.slave

class Decoder extends Component {

  val io = new Bundle {
    val inst = slave Flow (Bits(width = 32 bits))
    val opcodes = out UInt (width = 5 bits)
    val format = out UInt (width = 3 bits)
    val rd = out UInt (width = 5 bits)
    val rs1 = out UInt (width = 5 bits)
    val rs2 = out UInt (width = 5 bits)
    val imm = out Bits (width = 32 bits)
  }

  def setDefaultValues(): Unit = {
    io.inst.valid := False
    io.inst.payload := 0
  }

  private val fields = new Area {
    val opcode_6_2 = io.inst.payload(6 downto 2).asUInt
    val rd = io.inst.payload(11 downto 7).asUInt
    val rs1 = io.inst.payload(19 downto 15).asUInt
    val rs2 = io.inst.payload(24 downto 20).asUInt
    val function3 = io.inst.payload(14 downto 12).asUInt
    val function7Significant = io.inst.payload(30)
  }

  io.format := 0
  io.rd := fields.rd
  io.rs1 := fields.rs1
  io.rs2 := fields.rs2
  io.imm := 0
  io.opcodes := fields.function3.resize(io.opcodes.getWidth)

  val RDecoder = new Area {
    val isRInstruction = fields.opcode_6_2 === U(0x0C)
    when(isRInstruction) {
      io.format := InstructionFormat.RFormat
      mapALUCode(fields.function3, fields.function7Significant, io.opcodes, isRFormat = true)
    }
  }

  val ImmDecoder = new Area {
    val isImmInstruction = fields.opcode_6_2 === U(0x04)
    val imm = SInt(width = 12 bits)
    val shiftAmount = io.inst.payload(25 downto 20).asSInt
    imm := 0
    val immSignExtended = imm.resize(width = 32 bits)
    when(isImmInstruction) {
      io.format := InstructionFormat.IFormat
      mapALUCode(fields.function3, fields.function7Significant, io.opcodes, isRFormat = false)
      when(fields.function3 === 5 || fields.function3 === 1) {
        imm := shiftAmount.resize(12)
      } otherwise {
        imm := io.inst.payload(31 downto 20).asSInt
      }
      io.imm := immSignExtended.asBits
    }
  }

  val ImmLoadDecoder = new Area {
    val isImmLoadInstruction = fields.opcode_6_2 === U(0x00)
    val immSigned = io.inst.payload(31 downto 20).asSInt
    val immUnsigned = immSigned.asUInt
    val signedByte = io.inst.payload(27 downto 20).asSInt
    val unsignedByte = signedByte.asUInt

    val immSignedExtended = immSigned.resize(width = 32 bits)
    val immZeroExtended = immUnsigned.resize(width = 32 bits)
    val byteSignExtended = signedByte.resize(width = 32 bits)
    val byteZeroExtended = unsignedByte.resize(width = 32 bits)
    when(isImmLoadInstruction) {
      io.format := InstructionFormat.ILoadFormat
      io.opcodes := fields.function3.resize(io.opcodes.getWidth)
      switch(fields.function3) {
        is(0) { //load byte
          io.imm := byteSignExtended.asBits //TODO
        }
        is(1) { //lh
          io.imm := immSignedExtended.asBits //TODO
        }
        is(2) { //lw
          io.imm := immSignedExtended.asBits
        }
        is(4) { //lbu
          io.imm := byteZeroExtended.asBits
        }
        is(5) { //lhu
          io.imm := immZeroExtended.asBits
        }
      }
    }

    val isJALRFormatInstruction = fields.opcode_6_2 === U(0x19)
    when(isJALRFormatInstruction) {
      io.format := InstructionFormat.IJumpFormat
      io.imm := immSignedExtended.asBits
    }
  }

  val BDecoder = new Area {
    val isBFormatInstruction = fields.opcode_6_2 === U(0x18)
    val imm = io.inst.payload(31) ## io.inst.payload(7) ## io.inst.payload(30 downto 25) ## io.inst.payload(11 downto 8) ## False
    when(isBFormatInstruction) {
      io.format := InstructionFormat.BFormat
      io.imm := imm.resize(32 bits)
      io.opcodes := fields.function3.resize(io.opcodes.getWidth)
    }
  }

  val JDecoder = new Area {
    val isJFormatInstruction = fields.opcode_6_2 === U(0x1b)
    val imm = io.inst.payload(31) ## io.inst.payload(19 downto 12) ## io.inst.payload(20) ## io.inst.payload(30 downto 21) ## False
    when(isJFormatInstruction) {
      io.format := InstructionFormat.JFormat
      io.imm := imm.resize(32 bits)
      // don't care about opcodes
    }
  }

  val UDecoder = new Area {
    val isLUIInstruction = fields.opcode_6_2 === U(0x0D)
    val isAUIPCInstruction = fields.opcode_6_2 === U(0x05)
    val imm = io.inst.payload(31 downto 12)
    //    io.rs1 := 0
    when(isLUIInstruction) {
      io.format := InstructionFormat.UFormat
      io.opcodes := OpCodes.LUI
      io.imm := imm ## B(0, 12 bits)
    }
    when(isAUIPCInstruction) {
      io.format := InstructionFormat.UFormat
      io.opcodes := OpCodes.AUIPC
      io.imm := imm ## B(0, 12 bits)
    }
  }

  val SDecoder = new Area {
    val isSFormat = fields.opcode_6_2 === U(0x08)
    val imm = io.inst.payload(31 downto 25) ## io.inst.payload(11 downto 7)
    val immSigned = imm.asSInt
    val immSignExtended = immSigned.resize(32 bits)

    when(isSFormat) {
      io.format := InstructionFormat.SFormat
      io.imm := immSignExtended.asBits
    }
  }

  def mapALUCode(function: UInt, function7: Bool, code: UInt, isRFormat: Boolean): Unit = {
    switch(function) {
      is(0) {
        if (isRFormat) {
          when(function7 === False) {
            code := ALUOpCodes.ADD
          } otherwise {
            code := ALUOpCodes.SUB
          }
        } else {
          code := ALUOpCodes.ADD
        }
      }
      is(5) {
        when(function7 === False) {
          code := ALUOpCodes.SRL
        } otherwise {
          code := ALUOpCodes.SRA
        }
      }
      default {
        code := function.resize(5 bits)
      }
    }
  }
}

object DecoderVerilog {
  def main(args: Array[String]) {
    SpinalVerilog(new Decoder)
  }
}
