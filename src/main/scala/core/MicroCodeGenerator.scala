package core

import spinal.core._

import scala.language.postfixOps

object MicroCodes {
  def ALU_SRC_REG = 0
  def ALU_SRC_IMM = 1
}

class MicroCodeGenerator(decoder: Decoder) extends Area {

  val aluSrc1: UInt = UInt(width = 1 bits)
  val aluSrc2: UInt = UInt(width = 1 bits)
  val aluOpSrc: UInt = UInt(width = 2 bits)
  val aluOpCodes: decoder.io.opcodes.type = decoder.io.opcodes.clone()

  val regWriteEnable: Bool = (decoder.io.format =/= OpCodes.InstructionFormat.SFormat
    && decoder.io.format =/= OpCodes.InstructionFormat.BFormat)

//  switch(decoder.io.format) {
//    is(OpCodes.InstructionFormat.SFormat) {
//      regWriteEnable := False
//    }
//    is(OpCodes.InstructionFormat.BFormat) {
//      regWriteEnable := False
//    }
//    default {
//      regWriteEnable := True
//    }
//  }

  // incorrect s1; all zero;
  switch(decoder.io.format) {
    is(OpCodes.InstructionFormat.UFormat) {
      aluSrc1 := MicroCodes.ALU_SRC_IMM
    }
    is(OpCodes.InstructionFormat.JFormat) {
      aluSrc1 := MicroCodes.ALU_SRC_IMM
    }
    default {
      aluSrc1 := MicroCodes.ALU_SRC_REG
    }
  }

  switch(decoder.io.format) {
    is(OpCodes.InstructionFormat.UFormat) {
      aluSrc2 := MicroCodes.ALU_SRC_IMM
    }
    is(OpCodes.InstructionFormat.IFormat) {
      aluSrc2 := MicroCodes.ALU_SRC_IMM
    }
    is(OpCodes.InstructionFormat.JFormat) {
      aluSrc2 := MicroCodes.ALU_SRC_IMM
    }
    default {
      aluSrc2 := MicroCodes.ALU_SRC_REG
    }
  }

  switch(decoder.io.format) {
    is(OpCodes.InstructionFormat.RFormat) {
      aluOpCodes := decoder.io.opcodes
    }
    is(OpCodes.InstructionFormat.IFormat) {
      aluOpCodes := decoder.io.opcodes
    }
    is(OpCodes.InstructionFormat.BFormat) {
      aluOpCodes := OpCodes.ALUOpCodes.SUB
    }
    is(OpCodes.InstructionFormat.UFormat) {
      aluOpCodes := OpCodes.ALUOpCodes.ADD
    }
    default {
      aluOpCodes := OpCodes.ALUOpCodes.ADD
    }
  }
}


