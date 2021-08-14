import isa.Assembler
import sim.InstructionFetcherSim
import spinal.core.sim._

class InstructionFetcherTest extends BaseTest[InstructionFetcherSim] {
  behavior of "InstructionFetcher"

  override def withWave = true

  it should "" in {
    simulator.doSim { dut =>
      dut.clockDomain.forkStimulus(period = 10)
      val assembler = new Assembler()
      val instructions = assembler.assembleFile("test/imm.s")
      dut.fillCache(instructions)

      for (index <- 0 until 1000) {
        dut.io.address.valid #= true
        dut.io.address.payload #= index
        dut.clockDomain.waitSampling()
        if (index < instructions.length) {
          val inst = instructions(index)
          //          assert(dut.io.instruction.payload.toBigInt == inst.toBigInt)
        }
      }
    }
  }

  override def dut(): InstructionFetcherSim = new InstructionFetcherSim
}
