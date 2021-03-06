package core

import spinal.core._
import spinal.lib.{master, slave}

class InstructionFetcher() extends Component {
  val io = new Bundle {
    val address = slave Flow(UInt(width = 32 bits))
    val instruction = master Flow(Bits(width = 32 bits))
  }
  val cacheConfig = CacheConfig(
    width = 32 bits,
    depth = 4,
    rows = 32
  )
  val instructionCache = new Cache(cacheConfig)
  instructionCache.io.address <> io.address
  io.instruction <> instructionCache.io.data

}

object InstructionFetcherVerilog {
  def main(args: Array[String]) {
    SpinalVerilog(new InstructionFetcher())
  }
}
