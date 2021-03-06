import sim.RegisterFileSim
import spinal.core.sim.SimClockDomainPimper

import scala.util.Random

class RegisterFileTest extends BaseTest [RegisterFileSim] {
  behavior of "RegisterFile"

  it should "sync with RegFile" in {
    simulator.doSim { dut =>
      dut.clockDomain.forkStimulus(period = 10)
      var rd = 3
      var rs1 = 0
      var rs2 = 0
      for (_ <- 0 to 1) {
        val rdData = Random.nextInt(Integer.MAX_VALUE)
        dut.regFile.write(rd, rdData)
        dut.regFile.checkMatch()
        dut.clockDomain.waitSampling()
        dut.regFile.tick()
        dut.regFile.checkMatch()
        assert(dut.regFile.read(rd) == rdData.toLong )
      }
    }
  }
  override def dut() : RegisterFileSim  = new RegisterFileSim
}
