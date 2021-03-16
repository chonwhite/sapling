package core

import isa.Assembler
import spinal.core._
import spinal.lib.{master, slave}

import scala.collection.mutable.ArrayBuffer

class InstructionFetcher() extends Component {
  val io = new Bundle {
    val address = slave Flow(UInt(width = 32 bits))
    val instruction = master Flow(Bits(width = 32 bits))
  }

  val assembler = new Assembler()
  var assemblyFile = "test/imm.S"
  assemblyFile = "test/branch.S"
  val instructions = assembler.assembleFile(assemblyFile)
  val codes = ArrayBuffer[BigInt]()
  for (inst <- instructions) {
    codes += inst.toBigInt
  }

  val cacheConfig = CacheConfig(
    width = 32 bits,
    depth = 4,
    rows = 32,
    content = codes.toArray
  )

  val instructionCache = new InstructionCache(cacheConfig) //TODO
  instructionCache.io.address <> io.address
  io.instruction <> instructionCache.io.data
}

object InstructionFetcherVerilog {
  def main(args: Array[String]) {
    SpinalVerilog(new InstructionFetcher())
  }
}
