
import sim.ALUSim
import sim.SimToolkit._

class ALUTest extends BaseTest [ALUSim]{
  behavior of "ALU"

  def sim_op(op: Int) {
    simulator.doSim{ dut =>
      for (_ <- 0 to 5) {
        dut.simOp(op)
      }
    }
    println("op %d success!".format(op))
  }

  it must "eval result correctly" in {
    for (op <- ROps) {
      sim_op(op)
    }
  }

  override def dut() : ALUSim = new ALUSim
}
