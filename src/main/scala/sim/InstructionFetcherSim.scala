package sim

import core.InstructionFetcher
import isa.Assembler
import spinal.core.sim._

object InstructionFetcherSim {

  def simAssembly(path: String): Unit = {


    // SimConfig.withWave.compile {
    //   val dut = new InstructionFetcher(null)
    //   for (reg <- dut.instructionCache.registers) {
    //     reg.simPublic()
    //   }
    //   dut
    // }.doSim { dut =>
    //   dut.clockDomain.forkStimulus(period = 10)
    //   val assembler = new Assembler()
    //   val instructions = assembler.readFile(path)
    //   for (inst <- instructions) {
    //     println(inst)
    //     dut.io.inst.payload #= inst.toBigInt
    //     sleep(1)
    //     print(dut.io.format.toLong)
    //     print(dut.io.imm.toLong)
    //     print(dut.io.rd.toLong)
    //     print(dut.io.rs1.toLong)
    //     print(dut.io.rs2.toLong)
    //   }
    // }
  }

  def main(args: Array[String]): Unit = {
    simAssembly("test/imm.s")
  }
}


