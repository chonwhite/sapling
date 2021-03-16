package isa

import core.OpCodes

import scala.collection.immutable.HashMap

object RV32I {

  val registerNames = HashMap(
    "zero" -> 0, "ra" -> 1, "sp" -> 2, "gp" -> 3,
    "gp" -> 4, "t0" -> 5, "t1" -> 6, "t2" -> 7,
    "s0" -> 8, "fp" -> 8, "s0" -> 9,
    "a0" -> 10, "a1" -> 11, "a2" -> 12, "a3" -> 13,
    "a4" -> 14, "a5" -> 15, "a6" -> 16, "a7" -> 17,
    "s2" -> 18, "s3" -> 19, "s4" -> 20, "s5" -> 21,
    "s6" -> 22, "s7" -> 23, "s8" -> 24,
    "s9" -> 25, "s10" -> 26, "s11" -> 27,
    "t3" -> 28, "t4" -> 29, "t5" -> 30, "t6" -> 31
  )

  val RNameToALUOp = HashMap(
    "add" -> OpCodes.ALUOpCodes.ADD,
    "sub" -> OpCodes.ALUOpCodes.SUB,
    "sll" -> OpCodes.ALUOpCodes.SLL,
    "slt" -> OpCodes.ALUOpCodes.SLT,
    "sltu" -> OpCodes.ALUOpCodes.SLTU,
    "xor" -> OpCodes.ALUOpCodes.XOR,
    "srl" -> OpCodes.ALUOpCodes.SRL,
    "sra" -> OpCodes.ALUOpCodes.SRA,
    "or" -> OpCodes.ALUOpCodes.OR,
    "and" -> OpCodes.ALUOpCodes.AND)

  val ROpToName = Map() ++ RNameToALUOp.map(_.swap)

  val INameToALUOp = HashMap(
    "addi" -> OpCodes.ALUOpCodes.ADD,
    "slli" -> OpCodes.ALUOpCodes.SLL,
    "slti" -> OpCodes.ALUOpCodes.SLT,
    "sltiu" -> OpCodes.ALUOpCodes.SLTU,
    "xori" -> OpCodes.ALUOpCodes.XOR,
    "srli" -> OpCodes.ALUOpCodes.SRL,
    "srai" -> OpCodes.ALUOpCodes.SRA,
    "ori" -> OpCodes.ALUOpCodes.OR,
    "andi" -> OpCodes.ALUOpCodes.AND)

  val BNameToOp = HashMap(
    "beq" -> OpCodes.BranchOpCodes.BEQ,
    "bne" -> OpCodes.BranchOpCodes.BNE,
    "blt" -> OpCodes.BranchOpCodes.BLT,
    "bge" -> OpCodes.BranchOpCodes.BGE,
    "bltu" -> OpCodes.BranchOpCodes.BLTU,
    "bgeu" -> OpCodes.BranchOpCodes.BGEU
  )

  val IOpToName = Map() ++ INameToALUOp.map(_.swap)

  def isRFormat(name: String): Boolean = {
    RNameToALUOp.keySet.contains(name)
  }

  def isIFormat(name: String): Boolean = {
    INameToALUOp.keySet.contains(name)
  }

  def isBFormat(name: String): Boolean = {
    BNameToOp.keySet.contains(name)
  }

  def IInstruction(name: String, args: Int*): IInstruction = {
    val inst = new IInstruction(args(0), args(1), args(2), INameToALUOp(name))
    inst.name = name
    inst
  }

  def BInstruction(name: String, args: Int*): BInstruction = {
    val inst = new BInstruction(args(0), args(1), args(2), BNameToOp(name))
    inst.name = name
    inst
  }

  def ADD(rd: Int, rs1: Int, rs2: Int): Instruction = {
    RInstruction("name", rd, rs1, rs2)
  }

  def SUB(rd: Int, rs1: Int, rs2: Int): Instruction = {
    RInstruction("sub", rd, rs1, rs2)
  }

  def SLL(rd: Int, rs1: Int, rs2: Int): Instruction = {
    RInstruction("sll", rd, rs1, rs2)
  }

  def RInstruction(name: String, args: Int*): RInstruction = {
    val inst = new RInstruction(args(0), args(1), args(2), RNameToALUOp(name))
    inst.name = name
    inst
  }

  def SLT(rd: Int, rs1: Int, rs2: Int): Instruction = {
    new RInstruction(rd, rs1, rs2, OpCodes.ALUOpCodes.SLT)
  }

  def SLTU(rd: Int, rs1: Int, rs2: Int): Instruction = {
    new RInstruction(rd, rs1, rs2, OpCodes.ALUOpCodes.SLTU)
  }

  def XOR(rd: Int, rs1: Int, rs2: Int): Instruction = {
    new RInstruction(rd, rs1, rs2, OpCodes.ALUOpCodes.XOR)
  }

  def SRL(rd: Int, rs1: Int, rs2: Int): Instruction = {
    new RInstruction(rd, rs1, rs2, OpCodes.ALUOpCodes.SRL)
  }

  def SRA(rd: Int, rs1: Int, rs2: Int): Instruction = {
    new RInstruction(rd, rs1, rs2, OpCodes.ALUOpCodes.SRA)
  }

  def OR(rd: Int, rs1: Int, rs2: Int): Instruction = {
    new RInstruction(rd, rs1, rs2, OpCodes.ALUOpCodes.OR)
  }

  def AND(rd: Int, rs1: Int, rs2: Int): Instruction = {
    new RInstruction(rd, rs1, rs2, OpCodes.ALUOpCodes.AND)
  }

  def ADDI()(rd: Int, rs1: Int, imm: Int): Instruction = {
    new IInstruction(rd, rs1, imm, OpCodes.ALUOpCodes.ADD)
  }

  def SLLI(rd: Int, rs1: Int, imm: Int): Instruction = {
    new IInstruction(rd, rs1, imm, OpCodes.ALUOpCodes.SLL)
  }

  def SLTI(rd: Int, rs1: Int, imm: Int): Instruction = {
    new IInstruction(rd, rs1, imm, OpCodes.ALUOpCodes.SLT)
  }

  def SLTIU(rd: Int, rs1: Int, imm: Int): Instruction = {
    new IInstruction(rd, rs1, imm, OpCodes.ALUOpCodes.SLTU)
  }

  def XORI(rd: Int, rs1: Int, imm: Int): Instruction = {
    new IInstruction(rd, rs1, imm, OpCodes.ALUOpCodes.XOR)
  }

  def SRLI(rd: Int, rs1: Int, imm: Int): Instruction = {
    new IInstruction(rd, rs1, imm, OpCodes.ALUOpCodes.SRL)
  }

  def SRAI(rd: Int, rs1: Int, imm: Int): Instruction = {
    new IInstruction(rd, rs1, imm, OpCodes.ALUOpCodes.SRA)
  }

  def ORI(rd: Int, rs1: Int, imm: Int): Instruction = {
    new IInstruction(rd, rs1, imm, OpCodes.ALUOpCodes.OR)
  }

  def ANDI(rd: Int, rs1: Int, imm: Int): Instruction = {
    new IInstruction(rd, rs1, imm, OpCodes.ALUOpCodes.AND)
  }

  class Opcodes(val name: String, val op: Int)
}



