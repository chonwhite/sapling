import sim.ControlUnitSim

class ControlUnitTest extends BaseTest[ControlUnitSim] {
  behavior of "ControlUnit"

    it must "xxx" in {
      simulator.doSim{
        dut =>
        dut.simFile("test/led.S")
      }
    }

  it must "ligth led" in {
    simulator.doSim {
      dut =>
        dut.simCycles(200)
    }
  }

  override def withWave = true

  override def dut(): ControlUnitSim = {
    println("dut")
    new ControlUnitSim
  }
}
