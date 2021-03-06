package sim

import core.InstructionFetcher
import isa.Instruction
import spinal.core.sim._

class InstructionFetcherSim extends InstructionFetcher {
  for (reg <- instructionCache.registers) {
    reg.simPublic()
  }

  def fillCache(instructions: Array[Instruction]): Unit = {
    for (index <- instructions.indices) {
      val inst = instructions(index)
      instructionCache.registers(index) #= inst.toBigInt
    }
  }
}


