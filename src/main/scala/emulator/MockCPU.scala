package emulator

import core.OpCodes.ALUOpCodes._
import core.OpCodes.InstructionFormat._
import isa._

class MockCPU {
  val registerFile = new RegisterFile()

  def execInstruction(instruction: Instruction): Unit = {
    var opcode = 0
    var A = 0L
    var B = 0L
    var rd = 0L
    instruction.instructionFormat() match {
      case RFormat =>
        val r = instruction.asInstanceOf[RInstruction]
        A = registerFile.getRegister(r.rs1)
        B = registerFile.getRegister(r.rs2)
        opcode = r.opcode
        rd = r.rd
        registerFile.setRegister(r.rd, alu(opcode, A, B));
      case IFormat =>
        val i = instruction.asInstanceOf[IInstruction]
        A = registerFile.getRegister(i.rs1)
        B = i.imm
        opcode = i.opcode
        registerFile.setRegister(i.rd, alu(opcode, A, B));
      case BFormat =>
        println("B")
    }
  }

  def alu(op: Int, A: Long, B: Long): Long = {
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
}
