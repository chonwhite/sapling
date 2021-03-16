package sim

import core.{CacheConfig, InstructionCache}
import isa.Assembler
import spinal.core.sim._

class ICacheSim(config: CacheConfig) extends InstructionCache(config) {
  def simAssemblyFile(file: String): Unit = {
    val assembler = new Assembler()
    val instructions = assembler.assembleFile(file)

    for (index <- instructions.indices) {
      val inst = instructions(index)
      io.address.payload #= index
      io.address.valid #= true
      sleep(1)
      println(io.data.payload.toBigInt)
      println(inst.toBigInt)
      assert(io.data.valid.toBoolean)
      assert(io.data.payload.toBigInt == inst.toBigInt)
    }
  }

  def readAddress(address: Int): Unit = {
    io.address.payload #= address
    io.address.valid #= true
    sleep(1)
  }
}
