package core

import spinal.core.{Area, Bits, Bool, IntToBuilder, Reg, RegNextWhen, UInt, default, is, switch, when}
import spinal.lib.Flow

import scala.language.postfixOps

case class Control() extends Area{
  //payload is reg source selector;
  val regWrite : Flow[UInt] = Reg(Flow(UInt(width = 2 bits)))

  val memWriteEnable : Bool = Reg(Bool())
  val memSourceSelector : UInt = Reg(UInt(width = 4 bits))

  val jump : Bool = Reg(Bool())
  val branch : Bool = Reg(Bool())

  val aluSrc1Selector : Bits = Reg(Bits(width = 1 bits)) init(0)
  val aluSrc2Selector : Bits = Reg(Bits(width = 1 bits)) init(0)

  val aluOpCode : UInt = Reg(UInt(width = 5 bits))

//  val memWriteEnable
}

case class Stage1 (valid: Bool, pc: Flow[UInt], inst: Flow[Bits]) extends Area {
  val PC: Flow[UInt] = RegNextWhen(pc, valid)
  val instruction: Flow[Bits] = RegNextWhen(inst, valid)
}

case class Stage2 (valid: Bool, pc: Flow[UInt]) extends Area {
  val PC: Flow[UInt] = Reg(pc)
//  val opCode : UInt = RegNextWhen(op, valid); //TODO
  val control : Control = Control()

  val reg1 : Bits = Reg(Bits(width = 32 bits)) //TODO
  val reg2 : Bits = Reg(Bits(width = 32 bits))
  val immediateValue : Bits = Reg(Bits(width = 32 bits)) init(0)

  val rd : UInt = Reg(UInt(width = 5 bits))

  def connectToRegisterFile(registerFile: RegisterFile): Unit = {
    reg1 <> registerFile.io.read1_data
    reg2 <> registerFile.io.read2_data
  }

  def connectToALU(alu : ALU): Unit = {
    alu.io.op := control.aluOpCode
    alu.io.s1 := reg1

    when(control.aluSrc1Selector === 0) {
      alu.io.s2 := reg2
    }otherwise {
      alu.io.s2 := immediateValue
    }
  }

  def <<(registerFile: RegisterFile): Unit = {
    connectToRegisterFile(registerFile)
  }

  def >>(alu: ALU): Unit = {
    connectToALU(alu)
  }

  def genControlSignals(decoder: Decoder): Unit = {
//    val aluSrc1: UInt = UInt(width = 1 bits)
    val aluSrc2: Bits = control.aluSrc2Selector
//    val aluOpSrc: UInt = UInt(width = 2 bits)
    val aluOpCodes: UInt = control.aluOpCode

    control.regWrite.valid := (decoder.io.format =/= OpCodes.InstructionFormat.SFormat
      && decoder.io.format =/= OpCodes.InstructionFormat.BFormat)

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
      is(OpCodes.InstructionFormat.SFormat) {
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
      is(OpCodes.InstructionFormat.SFormat) {
        aluOpCodes := OpCodes.ALUOpCodes.ADD
      }
      default {
        aluOpCodes := OpCodes.ALUOpCodes.ADD
      }
    }
//    control.aluOpCode := aluOpCodes
  }

  def clear(): Unit = {
    control.aluOpCode := 0
//    control := 0 //TODO
    reg1 := 0
    reg2 := 0
    immediateValue := 0
    rd := 0
  }
}

case class Stage3 (){
  val control : Control = Control()

  val aluResult : Bits = Reg(Bits(width = 32 bits))
  val reg2 : Bits = Reg(Bits(width = 32 bits))

  def <<(alu: ALU): Unit = {
    aluResult := alu.io.res
  }
}

case class Stage4 (){
  val aluResult : Bits = Reg(Bits(width = 32 bits))
  val reg2 : Bits = Reg(Bits(width = 32 bits))

}
