import core.CacheConfig
import isa.Assembler
import sim.ICacheSim
import spinal.core.IntToBuilder

import scala.collection.mutable.ArrayBuffer

class ICacheTest extends BaseTest[ICacheSim] {
  behavior of "ALU"

  it must "read data" in {
    simulator.doSim { dut =>
      dut.simAssemblyFile("test/imm.s")
    }
  }

  override def dut(): ICacheSim = {
    println("dut")
    val assembler = new Assembler()
    val instructions = assembler.assembleFile("test/imm.s")
    val codes = ArrayBuffer[BigInt]()
    for (inst <- instructions) {
      codes += inst.toBigInt
    }

    new ICacheSim(CacheConfig(
      width = 32 bits,
      depth = 256,
      rows = 256,
      content = codes.toArray
    ))
  }

  override def withWave = true
}
