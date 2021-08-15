package core

import spinal.core._
import spinal.lib.{master, slave}

import scala.language.postfixOps

class InstructionFetcherBundle extends Bundle {
  val address = slave Flow UInt(width = 32 bits)
  val instruction = master Flow Bits(width = 32 bits)
}

class InstructionFetcher() extends Component {
  val io: InstructionFetcherBundle = new InstructionFetcherBundle()

  val instructionCache = ComponentFactory.instructionCache
  instructionCache.setConfig(GlobalConfig.cacheConfig());

  instructionCache.io.address <> io.address
  io.instruction <> instructionCache.io.data
}

object InstructionFetcherVerilog {
  def main(args: Array[String]): Unit = {
    SpinalVerilog(new InstructionFetcher())
  }
}
