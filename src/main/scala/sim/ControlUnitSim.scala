package sim

import core.ControlUnit
import emulator.MockCPU
import isa._
import spinal.core.sim._

class ControlUnitSim extends ControlUnit {

  val mockCPU = new MockCPU

  class InstructionValidator {

    def compRegisters(): Unit = {
      for (index <- 0 to 31) {
        if (mockCPU.registerFile.getRegister(index) != registerFile.registers(index).toLong) {
          println(index)
          println(mockCPU.registerFile.getRegister(index))
          println(registerFile.registers(index).toLong)
        }
//        assert(mockCPU.registerFile.getRegister(index) == registerFile.registers(index).toLong)
      }
    }

    def validate(instruction : Instruction): Unit = {
//      println(instruction)
//
//      mockCPU.execInstruction(instruction)
//      compRegisters()
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

      for (index <- 0 to 200 * 1000) {
        clockDomain.waitSampling()
//        validator.validate(instructions(index))
      }
  }

  def simCycles(cycles : Int) : Unit = {
    clockDomain.forkStimulus(period = 10)

    for (index <- 0 until cycles) {
      clockDomain.waitSampling()
    }
  }
}
