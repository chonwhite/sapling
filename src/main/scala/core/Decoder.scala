package core

import core.OpCodes._
import spinal.core._
import spinal.lib.{Flow, slave}

import scala.language.postfixOps

class DecoderBundle extends Bundle{
  val inst: Flow[Bits] = slave Flow Bits(width = 32 bits)
  val opcodes: UInt = out UInt(width = 5 bits)
  val format: UInt = out UInt(width = 3 bits)
  val rd: UInt = out UInt(width = 5 bits)
  val rs1: UInt = out UInt(width = 5 bits)
  val rs2: UInt = out UInt(width = 5 bits)
  val imm: Bits = out Bits(width = 32 bits)
}

class DecoderSlaveBundler extends DecoderBundle {
  opcodes.asInput()
  format.asInput()
  rd.asInput()
  rs1.asInput()
  rs2.asInput()
  imm.asInput()
}

class Decoder extends Component{
  noIoPrefix()
  val io = new DecoderBundle()

  private val fields = new Fields()
  io.format <> 0
  io.imm <> 0
  io.opcodes := fields.function3.resize(io.opcodes.getWidth)

  val rDecoder = new RDecoder()
  val immDecoder = new ImmDecoder()
  val immLoadDecoder = new ImmLoadDecoder()
  val bDecoder = new BDecoder()
  val jDecoder = new JDecoder()
  val uDecoder = new UDecoder()
  val sDecoder = new SDecoder()

  when (uDecoder.isUInstruction || jDecoder.isJFormatInstruction) {
    io.rs1 := 0
  } otherwise {
    io.rs1 := fields.rs1
  }

  when(rDecoder.isRInstruction || sDecoder.isSFormat || bDecoder.isBFormatInstruction) {
    io.rs2 := fields.rs2
  } otherwise {
    io.rs2 := 0
  }

  when(sDecoder.isSFormat || bDecoder.isBFormatInstruction) {
    io.rd := 0
  } otherwise {
    io.rd := fields.rd
  }

  def mapALUCode(function : UInt, function7 : Bool, code: UInt, isRFormat : Boolean): Unit = {
    switch(function){
      is(0) {
        if (isRFormat) {
          when(function7 === False) {
            code := ALUOpCodes.ADD
          }otherwise {
            code := ALUOpCodes.SUB
          }
        } else {
          code := ALUOpCodes.ADD
        }
      }
      is(5) {
        when(function7 === False) {
          code := ALUOpCodes.SRL
        }otherwise {
          code := ALUOpCodes.SRA
        }
      }
      default {
        code := function.resize(5 bits)
      }
    }
  }

  class Fields extends Area {
    val opcode_6_2: UInt = io.inst.payload(6 downto 2).asUInt
    val rd: UInt = io.inst.payload(11 downto 7).asUInt
    val rs1: UInt = io.inst.payload(19 downto 15).asUInt
    val rs2: UInt = io.inst.payload(24 downto 20).asUInt
    val function3: UInt = io.inst.payload(14 downto 12).asUInt
    val function7Significant: Bool = io.inst.payload(30)
  }

  class ImmDecoder extends Area {
    val isImmInstruction: Bool = fields.opcode_6_2 === U(0x04)
    val imm: SInt = SInt(width = 12 bits)
    val shiftAmount: SInt = io.inst.payload(25 downto 20).asSInt
    imm := 0
    val immSignExtended: SInt = imm.resize(width = 32 bits)
    when(isImmInstruction) {
      io.format := InstructionFormat.IFormat
      mapALUCode(fields.function3, fields.function7Significant, io.opcodes, isRFormat = false)
      when(fields.function3 === 5 || fields.function3 === 1) {
        imm := shiftAmount.resize(12)
      }otherwise {
        imm := io.inst.payload(31 downto 20).asSInt
      }
      io.imm := immSignExtended.asBits
    }
  }

  class RDecoder extends Area{
    val isRInstruction: Bool = fields.opcode_6_2 === U(0x0C)
    when(isRInstruction) {
      io.format := InstructionFormat.RFormat
      mapALUCode(fields.function3, fields.function7Significant, io.opcodes, isRFormat = true)
    }
  }

  class SDecoder extends Area{
    val isSFormat: Bool = fields.opcode_6_2 === U(0x08)
    val imm: Bits = io.inst.payload(31 downto 25) ## io.inst.payload(11 downto 7)
    val immSigned: SInt = imm.asSInt
    val immSignExtended: SInt = immSigned.resize(32 bits)

    when(isSFormat) {
      io.format := InstructionFormat.SFormat
      io.imm := immSignExtended.asBits
    }
  }

  class JDecoder extends Area {
    val isJFormatInstruction: Bool = fields.opcode_6_2 === U(0x1b)
    val imm: Bits = io.inst.payload(31) ## io.inst.payload(19 downto 12) ##
        io.inst.payload(20) ## io.inst.payload(30 downto 21) ## False
    when(isJFormatInstruction) {
      io.format := InstructionFormat.JFormat
      io.imm := imm.resize(32 bits)
      // don't care about opcodes
    }
  }

  class UDecoder extends Area {
    val isLUIInstruction: Bool = fields.opcode_6_2 === U(0x0D)
    val isAUIPCInstruction: Bool = fields.opcode_6_2 === U(0x05)
    val imm: Bits = io.inst.payload(31 downto 12)
    val isUInstruction: Bool = isLUIInstruction || isAUIPCInstruction

    when(isLUIInstruction) {
      io.format := InstructionFormat.UFormat
      io.opcodes := OpCodes.LUI
      io.imm := imm ## B(0, 12 bits)
    }
    when(isAUIPCInstruction){
      io.format := InstructionFormat.UFormat
      io.opcodes := OpCodes.AUIPC
      io.imm := imm ## B(0, 12 bits)
    }
  }

  class BDecoder extends Area {
    val isBFormatInstruction: Bool = fields.opcode_6_2 === U(0x18)
    val imm: Bits = io.inst.payload(31) ## io.inst.payload(7) ## io.inst.payload(30 downto 25) ## io.inst.payload(11 downto 8) ## False
    when(isBFormatInstruction) {
      io.format := InstructionFormat.BFormat
      io.imm := imm.resize(32 bits)
      io.opcodes := fields.function3.resize(io.opcodes.getWidth)
    }
  }

  class ImmLoadDecoder extends Area{
    val isImmLoadInstruction: Bool = fields.opcode_6_2 === U(0x00)
    val immSigned: SInt = io.inst.payload(31 downto 20).asSInt
    val immUnsigned: UInt = immSigned.asUInt
    val signedByte: SInt = io.inst.payload(27 downto 20).asSInt
    val unsignedByte: UInt = signedByte.asUInt

    val immSignedExtended: SInt = immSigned.resize(width = 32 bits)
    val immZeroExtended: UInt = immUnsigned.resize(width = 32 bits)
    val byteSignExtended: SInt = signedByte.resize(width = 32 bits)
    val byteZeroExtended: UInt = unsignedByte.resize(width = 32 bits)
    when(isImmLoadInstruction) {
      io.format := InstructionFormat.ILoadFormat
      io.opcodes := fields.function3.resize(io.opcodes.getWidth)
      switch(fields.function3) {
        is(0) {//load byte
          io.imm := byteSignExtended.asBits //TODO
        }
        is(1) {//lh
          io.imm := immSignedExtended.asBits //TODO
        }
        is(2) {//lw
          io.imm := immSignedExtended.asBits
        }
        is(4) {//lbu
          io.imm := byteZeroExtended.asBits
        }
        is(5) {//lhu
          io.imm := immZeroExtended.asBits
        }
      }
    }

    val isJALRFormatInstruction: Bool = fields.opcode_6_2 === U(0x19)
    when(isJALRFormatInstruction) {
      io.format := InstructionFormat.IJumpFormat
      io.imm := immSignedExtended.asBits
    }
  }
}

object DecoderVerilog {
  def main(args: Array[String]): Unit = {
    SpinalVerilog(new Decoder)
  }
}
