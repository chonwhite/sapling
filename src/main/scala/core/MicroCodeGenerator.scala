package core

import spinal.core._

import scala.language.postfixOps

object MicroCodes {
  def ALU_SRC_REG = 0
  def ALU_SRC_IMM = 1

}

class MicroCodeGenerator(decoder: Decoder) extends Area{

  val aluSrc1 = UInt(width = 1 bits)
  val aluSrc2 = UInt(width = 1 bits)
  val aluOpSrc = UInt(width = 2 bits)
  val aluOpCodes = decoder.io.opcodes.clone()
  val regWriteEnable = Bool()

  regWriteEnable := False //TODO

  // incorrect s1; all zero;

  switch(decoder.io.format) {
    is(OpCodes.InstructionFormat.UFormat) {
      aluSrc1 :=  MicroCodes.ALU_SRC_IMM
    }
    is(OpCodes.InstructionFormat.JFormat) {
      aluSrc1 :=  MicroCodes.ALU_SRC_IMM
    }
    default {
      aluSrc1 :=  MicroCodes.ALU_SRC_REG
    }
  }
  // TODO
  when(decoder.io.format === OpCodes.InstructionFormat.UFormat) {
    aluSrc2 :=  MicroCodes.ALU_SRC_REG
  } otherwise {
    aluSrc2 :=  MicroCodes.ALU_SRC_IMM
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
//    is(OpCodes.InstructionFormat.JFormat) {
//
//      ALUData.opcodes := OpCodes.ALUOpCodes.ADD
//      //      ALUData.A := data.address.asBits
//      ALUData.B := 4
//      data.imm := decoder.io.imm
//      data.op := OpCodes.PCOpCodes.ADD_OFFSET
//    }
//    is(OpCodes.InstructionFormat.SFormat) {
//      regFileData.enableResisters(Array[Boolean](true, true, false))
//      io.bus.address.valid := True
//      switch(decoder.io.opcodes) {
//        is (OpCodes.StoreOpcodes.STORE_WORD) {
//          io.bus.address.payload := decoder.io.imm.asUInt
//          io.bus.data.payload := registerFile.io.read2_data
//        }
//      }
//    }
  }

//  io.alu_src2 := 0
}


