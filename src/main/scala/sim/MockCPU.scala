package sim

import core.OpCodes.ALUOpCodes._
import core.OpCodes.InstructionFormat._
import isa._

import scala.collection.mutable.ArrayBuffer

class MockCPU {
  var registers = ArrayBuffer[Long]()
  for (_ <- 0 to 31) {
    registers += 0L
  }

  def setRegister(index: Int, value: Long): Unit = {
    if (index != 0) {
      registers(index) = value;
    }
  }

  def alu(op: Long, A: Long, B: Long): Long = {
    op match {
      case ADD =>
        A + B;
      case SUB =>
        A - B;
      case SLL =>
        A << B;
      case SLT =>
        if (A.intValue() < B.intValue()) 0 else 1
      case SLTU =>
        if (A < B) 0 else 1
      case XOR =>
        A ^ B
      case SRL =>
        A >>> B
      case SRA =>
        A >> B
      case OR =>
        A | B
      case AND =>
        A & B
    }
  }

  def execInstruction(instruction: Instruction): Unit = {
    var opcode = 0
    var A = 0L
    var B = 0L
    var rd = 0L
    instruction.instructionFormat() match {
      case RFormat =>
        val r = instruction.asInstanceOf[RInstruction]
        A = registers(r.rs1)
        B = registers(r.rs2)
        opcode = r.opcode
        rd = r.rd
        setRegister(r.rd ,alu(opcode, A, B));
      case IFormat =>
        val i = instruction.asInstanceOf[IInstruction]
        A = registers(i.rs1)
        B = i.imm
        opcode = i.opcode
        setRegister(i.rd ,alu(opcode, A, B));
    }

  }
}
