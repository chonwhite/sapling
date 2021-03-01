package isa

import core.OpCodes

import scala.collection.immutable.HashMap

object RV32I{

  val registerNames = HashMap(
    "zero"->0, "ra"->1, "sp"->2,"gp"->3,
    "gp"->4,"t0"->5,"t1"->6,"t2"->7,

  )

  val RInstructions = Array[String] ("add","sub","sll","slt","sltu","xor","srl","sra","or","and")


  def ADD(rd : Int, rs1 : Int, rs2 : Int): Instruction = {
    new RInstruction(rd, rs1, rs2, OpCodes.ALUOpCodes.ADD)
  }

  def SUB(rd : Int, rs1 : Int, rs2 : Int): Instruction = {
    new RInstruction(rd, rs1, rs2, OpCodes.ALUOpCodes.SUB)
  }

  def SLL(rd : Int, rs1 : Int, rs2 : Int): Instruction = {
    new RInstruction(rd, rs1, rs2, OpCodes.ALUOpCodes.SLL)
  }

  def SLT(rd : Int, rs1 : Int, rs2 : Int): Instruction = {
    new RInstruction(rd, rs1, rs2, OpCodes.ALUOpCodes.SLT)
  }

  def SLTU(rd : Int, rs1 : Int, rs2 : Int): Instruction = {
    new RInstruction(rd, rs1, rs2, OpCodes.ALUOpCodes.SLTU)
  }

  def XOR(rd : Int, rs1 : Int, rs2 : Int): Instruction = {
    new RInstruction(rd, rs1, rs2, OpCodes.ALUOpCodes.XOR)
  }

  def SRL(rd : Int, rs1 : Int, rs2 : Int): Instruction = {
    new RInstruction(rd, rs1, rs2, OpCodes.ALUOpCodes.SRL)
  }

  def SRA(rd : Int, rs1 : Int, rs2 : Int): Instruction = {
    new RInstruction(rd, rs1, rs2, OpCodes.ALUOpCodes.SRA)
  }

  def OR(rd : Int, rs1 : Int, rs2 : Int): Instruction = {
    new RInstruction(rd, rs1, rs2, OpCodes.ALUOpCodes.OR)
  }

  def AND(rd : Int, rs1 : Int, rs2 : Int): Instruction = {
    new RInstruction(rd, rs1, rs2, OpCodes.ALUOpCodes.AND)
  }

  def ADDI()(rd : Int, rs1 : Int, imm : Int): Instruction = {
    new IInstruction(rd, rs1, imm, OpCodes.ALUOpCodes.ADD)
  }

  def SLLI(rd : Int, rs1 : Int, imm : Int): Instruction = {
    new IInstruction(rd, rs1, imm, OpCodes.ALUOpCodes.SLL)
  }

  def SLTI(rd : Int, rs1 : Int, imm : Int): Instruction = {
    new IInstruction(rd, rs1, imm, OpCodes.ALUOpCodes.SLT)
  }

  def SLTIU(rd : Int, rs1 : Int, imm : Int): Instruction = {
    new IInstruction(rd, rs1, imm, OpCodes.ALUOpCodes.SLTU)
  }

  def XORI(rd : Int, rs1 : Int, imm : Int): Instruction = {
    new IInstruction(rd, rs1, imm, OpCodes.ALUOpCodes.XOR)
  }

  def SRLI(rd : Int, rs1 : Int, imm : Int): Instruction = {
    new IInstruction(rd, rs1, imm, OpCodes.ALUOpCodes.SRL)
  }

  def SRAI(rd : Int, rs1 : Int, imm : Int): Instruction = {
    new IInstruction(rd, rs1, imm, OpCodes.ALUOpCodes.SRA)
  }

  def ORI(rd : Int, rs1 : Int, imm : Int): Instruction = {
    new IInstruction(rd, rs1, imm, OpCodes.ALUOpCodes.OR)
  }

  def ANDI(rd : Int, rs1 : Int, imm : Int): Instruction = {
    new IInstruction(rd, rs1, imm, OpCodes.ALUOpCodes.AND)
  }
}



