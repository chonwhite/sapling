package sim

import core.{ControlUnit, InstructionFetcher}
import spinal.core.sim._

object ControlUnitSim {
  def main(args: Array[String]): Unit = {
    SimConfig.withWave.compile {
      val dut = new ControlUnit()
      for (reg <- dut.instructionFetcher.instructionCache.registers) {
        reg.simPublic()
      }
      dut
    }.doSim { dut =>
      dut.clockDomain.forkStimulus(period = 10)
      for ( index <- 0 to 31) {
        dut.instructionFetcher.instructionCache.registers(index) #= index * 10
      }

      for ( index <- 0 to 300) {
//        dut.io.address.valid #= true
//        dut.io.address.payload #= index
        //        dut.mem(0) #= 1.toLong
        //        sleep(1)
        dut.clockDomain.waitSampling()
      }
    }
  }
}
