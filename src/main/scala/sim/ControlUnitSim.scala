package sim

import core.ControlUnit
import isa.Assembler
import spinal.core.sim._

class ControlUnitSim extends ControlUnit {

  for (reg <- instructionFetcher.instructionCache.registers) {
    reg.simPublic()
  }

  def simFile(path: String): Unit = {
    SimConfig.withWave.compile {
      new ControlUnitSim()
    }.doSim { dut =>
      dut.clockDomain.forkStimulus(period = 10)
      val assembler = new Assembler()
      val instructions = assembler.assembleFile(path)

      for (index <- instructions.indices) {
        val inst = instructions(index)
        //        println(inst)
        //        println(inst.toBigInt.toLong.toHexString)
        dut.instructionFetcher.instructionCache.registers(index) #= inst.toBigInt
      }

      for (_ <- 0 to instructions.length) {
        dut.clockDomain.waitSampling()
      }
    }
  }
}
