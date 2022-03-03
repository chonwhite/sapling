package sim

import core.InstructionFetcher
import isa.Instruction

class InstructionFetcherSim extends InstructionFetcher {
//  instructionCache.mem.simPublic()

  def fillCache(instructions: Array[Instruction]): Unit = {
    for (index <- instructions.indices) {
      val inst = instructions(index)
//      instructionCache.mem.write(index, inst.toBigInt)
//      instructionCache.registers(index) #= inst.toBigInt
    }
  }
}


