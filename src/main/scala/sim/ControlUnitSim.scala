package sim

import core.ControlUnit
import emulator.MockCPU
import isa._
import spinal.core.sim._

class ControlUnitSim extends ControlUnit {

  val mockCPU = new MockCPU

  class InstructionValidator {

    def compRegisters(): Unit = {
//      for (index <- mockCPU.registers.indices) {
//        if (mockCPU.registers(index) != registerFile.registers(index).toLong) {
//          println(index)
//          println(mockCPU.registers(index))
//          println(registerFile.registers(index).toLong)
//        }
//        assert(mockCPU.registers(index) == registerFile.registers(index).toLong)
//      }
    }

    def validate(instruction : Instruction): Unit = {
      println(instruction)
      compRegisters()
      mockCPU.execInstruction(instruction)
    }
  }

  val validator = new InstructionValidator

  instructionFetcher.instructionCache.mem.simPublic()
  for (register <- registerFile.registers) {
    register.simPublic()
  }

  def simFile(path: String): Unit = {
      clockDomain.forkStimulus(period = 10)
      val assembler = new Assembler()
      val instructions = assembler.assembleFile(path)

      for (index <- instructions.indices) {
        clockDomain.waitSampling()
        validator.validate(instructions(index))
      }
  }
}
