package core

import spinal.core._
import spinal.lib.{Flow, master, slave}

import scala.language.postfixOps

//import GlobalCofnig

class InstructionFetcherBundle extends Bundle {
  val address: Flow[UInt] = slave Flow UInt(width = 32 bits)
  val instruction: Flow[Bits] = master Flow Bits(width = 32 bits)
}

class InstructionFetcher() extends Component {
  val io: InstructionFetcherBundle = new InstructionFetcherBundle()

  val instructionCache: InstructionCache = ComponentFactory.instructionCache()
  instructionCache.setConfig(GlobalConfig.cacheConfig())

  instructionCache.io.address <> io.address
  io.instruction <> instructionCache.io.data
}

object InstructionFetcherVerilog {
  def main(args: Array[String]): Unit = {
    SpinalVerilog(new InstructionFetcher())
  }
}
