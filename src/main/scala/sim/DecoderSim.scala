package sim

import core.Decoder
import core.OpCodes.InstructionFormat
import isa._
import sim.SimToolkit._
import spinal.core.sim._

import scala.util.Random

class DecoderSim extends Decoder {

  def simInstruction(instruction: Instruction): Unit = {
    io.inst.payload #= instruction.toBigInt
    sleep(1)
    assertInst(instruction)
  }

  def simRFormat(op: Int): Unit = {
    val dut = this
    for (rd <- 0 to 31) {
      for (rs1 <- 0 to 31) {
        for (rs2 <- 0 to 31) {
          val instruction = new RInstruction(rd, rs1, rs2, op)
          simInstruction(instruction)
        }
      }
    }
  }

  def assertInst(inst: Instruction): Unit = {
    assert(io.format.toInt == inst.instructionFormat)
    inst.instructionFormat() match {
      case InstructionFormat.RFormat =>
        assert(io.opcodes.toInt == inst.asInstanceOf[RInstruction].opcode)
        assert(io.rd.toInt == inst.asInstanceOf[RInstruction].rd)
        assert(io.rs1.toInt == inst.asInstanceOf[RInstruction].rs1)
        assert(io.rs2.toInt == inst.asInstanceOf[RInstruction].rs2)
      case InstructionFormat.IFormat =>
        assert(io.opcodes.toInt == inst.asInstanceOf[IInstruction].opcode)
        assert(io.rd.toInt == inst.asInstanceOf[IInstruction].rd)
        assert(io.rs1.toInt == inst.asInstanceOf[IInstruction].rs1)
        val immValue = bits32ToInt(io.imm.toLong)
        assert(immValue == inst.asInstanceOf[IInstruction].imm)
      case InstructionFormat.JFormat =>
//        assert(io.opcodes.toInt == inst.asInstanceOf[JInstruction].opcode)
        assert(io.rd.toInt == inst.asInstanceOf[JInstruction].rd)
        val immValue = bits32ToInt(io.imm.toLong)
        assert(immValue == inst.asInstanceOf[JInstruction].imm)
    }
  }

  def simIFormat(op: Int): Unit = {
    val dut = this
    val immRange = 1 << 11 - 1
    val halfRange = immRange / 2
    for (rd <- 0 to 31) {
      for (rs1 <- 0 to 31) {
        var imm = Random.nextInt(immRange) - halfRange
        if (shiftOps.contains(op)) {
          imm = Random.nextInt(31)
        }
        val instruction = new IInstruction(rd, rs1, imm, op)
        simInstruction(instruction)
      }
    }
  }
}
