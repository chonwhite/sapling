import sim.ControlUnitSim

class ControlUnitTest extends BaseTest [ControlUnitSim]{
  behavior of "ControlUnit"

  it must "xxx" in {
    simulator.doSim{
      dut =>
      dut.simFile("test/imm.s")
    }
  }

  override def withWave = true

  override def dut() : ControlUnitSim = {
    println("dut")
    new ControlUnitSim
  }
}
